(ns openrada.collector.core
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [openrada.collector.utils :as utils]))


(defn trim [text]
  (clojure.string/trim text))


(defn fetch-url
  ([url] (fetch-url url "windows-1251"))
  ([url encoding]
  (-> url
      java.net.URL.
      .getContent (java.io.InputStreamReader. encoding) ;<- encoding goes here
      html/html-resource)))


(defn short-name [full-name]
  (let [tokens (clojure.string/split full-name #" ")
        surname (first tokens)
        first-name (second tokens)
        father-name (last tokens)]
    (if (> (count tokens) 2)
      (str surname " " (.charAt first-name 0) "." (.charAt father-name 0) ".")
      (str surname " " (.charAt first-name 0) "."))))


;http://w1.c1.rada.gov.ua/pls/site2/fetch_mps?skl_id=8
(defn parse-members [page-url convocation]
  (let [page (fetch-url page-url)
        members (map (fn [node]
                    {:link (:href (trim (:attrs node)))
                     :convocation convocation
                     :full_name (trim (html/text node))
                     :short_name (trim (short-name (html/text node)))})
                       (html/select page [:ul :li :p.title :a]))]
      members))


(defn parse-members-8 []
  (parse-members "http://w1.c1.rada.gov.ua/pls/site2/fetch_mps?skl_id=9" 8))




(defn transform-month [month]
  (case month
    "січня" "01"
    "лютого" "02"
    "березня" "03"
    "квітня" "04"
    "травня" "05"
    "червня" "06"
    "липня" "07"
    "серпня" "08"
    "вересня" "09"
    "жовтня" "10"
    "листопада" "11"
    "грудня" "12"
    nil))



(defn transform-date [date]
  (let [date-str (str/trim date)
        tokens (str/split date-str #" ")
        year (str/join (take 4 (last tokens)))
        month (transform-month (second tokens))
        day (first tokens)]
    (str year "-" month "-" day)))



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
  (clojure.string/replace
     (clojure.string/replace value "Виборчому округу " "Виборчий округ ")
     "Загальнодержавному багатомандатному округу" "Загальнодержавний багатомандатний округ"))


(defn parse-dob [text]
  (transform-date
    (first
      (str/split text
       #"р\."))))



(defn parse-email [text]
  (if (.contains text "Ел. пошта:")
    (->
      (str/split text #"Ел. пошта:")
      (second)
      (str/trim))
    ""))

(defn parse-phone [text]
  (if (.contains text "Тел:")
    (str/trim
      (first
        (str/split
          (second
            (str/split text #"Тел:"))
          #"Ел. пошта:")))
    ""))




(defn parse-member [page-url]
  (let [page (fetch-url page-url "utf-8")
        faction (str/trim (last (str/split (str/trim (html/text (nth (html/select page [:table.simple_info :td ]) 1))) #"\n")))
        dob-text-str (html/text (nth (html/select page [:table.simple_info :td ]) 3))
        contact-str (str/join (map str/trim (map html/text (html/select page [:div.information_block_ins]))))
        roles-names (map str/trim (map html/text (html/select page [:ul.level1 :li])))
        roles-links (map (fn [node] (str/trim (:href (:attrs node)))) (html/select page [:ul.level1 :li :a]))
        roles (into {}
                    (map (fn [role link]
                     {:title role
                      :link link}) roles-names roles-links))
        image-url (:src (:attrs (first (html/select page [:table.simple_info :img]))))
        image (utils/fetch-image-as-base64 image-url)
        main-labels (map html/text (html/select page [:div.mp-general-info :dt]))
        main-values (map html/text (html/select page [:div.mp-general-info :dd]))
        merged (into {}
                     (filter identity
                       (map (fn [fe se]
                        {(transform-member-labels (first (clojure.string/split (trim fe) #":")))
                         (transform-member-values (trim se))}) main-labels main-values)))
        new-member-since-date (transform-date (get merged "member_since"))
        dob (parse-dob dob-text-str)
        email (parse-email contact-str)
        phone (parse-phone contact-str)
        merged (dissoc merged "member_since")]
      (assoc merged :dob dob
                    :email (trim email)
                    :phone (trim phone)
                    :faction (trim faction)
                    :roles roles
                    :image image
                    :member_since new-member-since-date)))


;(parse-member "http://gapp.rada.gov.ua/mps/info/page/18414")

;(map (fn [member]
;       (do
;         (println "xxx" member)
;        (try
;           (parse-member (:link member))
;           (catch Exception e (println "pppp" member)))
;         )
;       ) (parse-members-8))
