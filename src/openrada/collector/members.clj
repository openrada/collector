(ns openrada.collector.members
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [cuerdas.core :as str]
            [openrada.collector.utils :as utils]))


(defn short-name [full-name]
  (let [tokens (str/split full-name " ")
        surname (first tokens)
        first-name (second tokens)
        father-name (last tokens)]
    (if (> (count tokens) 2)
      (str surname " " (.charAt first-name 0) "." (.charAt father-name 0) ".")
      (str surname " " (.charAt first-name 0) "."))))



(defn parse-members
  ([convocation]
   (parse-members convocation "http://w1.c1.rada.gov.ua/pls/site2/fetch_mps?skl_id=9"))
  ([convocation page-url ]
    (let [page (utils/fetch-url page-url)
          members (map (fn [node]
                      {:link (str/trim (:href (:attrs node)))
                       :convocation convocation
                       :full_name (str/trim (html/text node))
                       :short_name (str/trim (short-name (html/text node)))})
                         (html/select page [:ul :li :p.title :a]))]
        members)))


(defn transform-member-labels [label]
  (case label
    "Обраний по" :district
    "Обрана по" :district
    "Регіон" :region
    "Партія" :party
    "Номер у списку" :rank_in_party
    "Дата набуття депутатських повноважень" "member_since"
    nil))

(defn transform-member-values [value]
  (str/replace
     (str/replace value "Виборчому округу " "Виборчий округ ")
     "Загальнодержавному багатомандатному округу" "Загальнодержавний багатомандатний округ"))


(defn parse-dob [text]
  (utils/transform-date
    (first
      (str/split text #"р\."))))



(defn parse-email [text]
  (if (str/contains? text "Ел. пошта:")
    (str/collapse-whitespace
      (first
        (str/split
          (second
            (str/split text "Ел. пошта:"))
          "Веб-сайт:")))
    ""))

(defn parse-phone [text]
  (if (str/contains? text "Тел:")
    (str/collapse-whitespace
      (first
        (str/split
          (second
            (str/split text "Тел:"))
          "Ел. пошта:")))
    ""))




(defn remove-last-char [s]
  (subs s 0 (- (count s) 1)))


(defn parse-member [page-url]
  (let [page (utils/fetch-url page-url "utf-8")
        dob-text-str (html/text (nth (html/select page [:table.simple_info :td ]) 3))
        notes-text-str (html/text (nth (html/select page [:table.simple_info :td ]) 5))
        contact-str (str/join (map str/trim (map html/text (html/select page [:div.information_block_ins]))))
        role-name (first (map str/trim (map html/text (html/select page [:ul.level1 :li]))))
        role-link (first (map (fn [node] (str/trim (:href (:attrs node)))) (html/select page [:ul.level1 :li :a])))
        role {:title (if (nil? role-name) nil (str/collapse-whitespace role-name))
              :link (str/trim role-link)}
        image-url (:src (:attrs (first (html/select page [:table.simple_info :img]))))
        image (utils/fetch-image-as-base64 image-url)
        main-labels (map html/text (html/select page [:div.mp-general-info :dt]))
        main-values (map html/text (html/select page [:div.mp-general-info :dd]))
        merged (into {}
                     (filter identity
                       (map (fn [fe se]
                        {(transform-member-labels (first (clojure.string/split (str/trim fe) #":")))
                         (transform-member-values (str/trim se))}) main-labels main-values)))
        new-member-since-date (utils/transform-date (get merged "member_since"))
        dob (parse-dob dob-text-str)
        email (parse-email contact-str)
        phone (parse-phone contact-str)
        merged (dissoc merged "member_since")
        notes (map str/clean (str/split notes-text-str ","))
        notes (assoc (vec notes) (- (count notes) 1) (remove-last-char (last notes)))]
      (assoc merged :dob dob
                    :email email
                    :phone phone
                    :position (if (nil? (:link role)) (:title role))
                    :image image
                    :notes notes
                    :member_since new-member-since-date)))


;(parse-member "http://gapp.rada.gov.ua/mps/info/page/18414")

