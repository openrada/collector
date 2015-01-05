(ns openrada.collector.committees
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [cuerdas.core :as str]
            [openrada.collector.utils :as utils]))

;http://w1.c1.rada.gov.ua/pls/site2/p_komitis
(defn parse-committees-list [page-url]
  (let [page (utils/fetch-url page-url)
        base-url (str/replace page-url "p_komitis" "")
        items (map (fn [node]
                    {:link (str base-url (str/trim (:href (:attrs node))))
                     :full_name (str/trim (html/text node))})
                       (html/select page [:table :a.topTitle]))]

    (filter (fn [item]
              (not (= (:link item) "http://w1.c1.rada.gov.ua/pls/site2/p_komity_free?skl=9"))
              ) items)))


;(parse-committees-list "http://w1.c1.rada.gov.ua/pls/site2/p_komitis")

(defn parse-committee-members [page-url]
  (let [page (utils/fetch-url page-url)
        rows (html/select page [:table :tr ])]
      (map (fn [row]
             (let [cells (filter (fn [item]
                                     (not (nil? (:tag item)))
                                     ) (:content row))
                   member (html/text (first cells))
                   role (html/text (last cells))]
               {:member member
                :role role}
             )) rows)
  ))
;(parse-committee-members "http://w1.c1.rada.gov.ua/pls/site2/p_komity_list?pidid=2629")



(defn parse-committee [committee]
  (let [page-url (:link committee)
        page (utils/fetch-url page-url)
        created-date-str (html/text (nth (html/select page [:table.simple_info :td ]) 1))
        created-at (utils/transform-date created-date-str)
        site-str (str/trim (:href (:attrs (nth (html/select page [:table.simple_info :td.topTitle :a ]) 1))))
        members-url (str/replace page-url "p_komity" "p_komity_list")
        members (parse-committee-members members-url)]
      {:created created-at
       :site (if (str/contains? site-str "rada.gov.ua") site-str nil)
       :members members
       :full_name (:full_name committee)
       :link (:link committee)}))


;(parse-committee {:link "http://w1.c1.rada.gov.ua/pls/site2/p_komity?pidid=2622"})

;(parse-committees-list "http://w1.c1.rada.gov.ua/pls/site2/p_komitis")
(defn parse-committees []
  (let [committees (parse-committees-list "http://w1.c1.rada.gov.ua/pls/site2/p_komitis")]
    (map parse-committee committees)))


;(parse-committees)