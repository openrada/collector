(ns openrada.collector.factions
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [cuerdas.core :as str]
            [openrada.collector.utils :as utils]))



;http://w1.c1.rada.gov.ua/pls/site2/p_fractions
(defn parse-factions-list [page-url convocation]
  (let [page (utils/fetch-url page-url)
        base-url (str/replace page-url "p_fractions" "")
        items (map (fn [node]
                    {:convocation convocation
                     :link (str base-url (utils/get-link node))
                     :name (str/trim (html/text node))})
                       (html/select page [:table :td.topTitle :a]))]

      (filter (fn [item]
              (str/contains? (:link item) "p_fraction?")
              ) items)))

;(parse-factions-list "http://w1.c1.rada.gov.ua/pls/site2/p_fractions" 8)

(defn parse-faction-members [page-url]
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

;(parse-faction-members "http://w1.c1.rada.gov.ua/pls/site2/p_fraction_list?pidid=2614")
(defn created-str [row-text]
  (last
     (str/split row-text "Дата створення: ")))

(defn parse-faction [faction]
  (let [page-url (:link faction)
        page (utils/fetch-url page-url)
        created-date-str (created-str (html/text (first (html/select page [:div.information_block_ins :p ]))))
        created-at (utils/transform-date created-date-str)
        members-url (str/replace page-url "p_fraction" "p_fraction_list")
        members (parse-faction-members members-url)]
      {:created created-at
       :convocation (:convocation faction)
       :members members
       :name (:name faction)
       :link (:link faction)}))


;(parse-faction {:link "http://w1.c1.rada.gov.ua/pls/site2/p_fraction?pidid=2614"})


(defn parse-factions [convocation]
  (let [factions (parse-factions-list "http://w1.c1.rada.gov.ua/pls/site2/p_fractions" convocation)]
    (map parse-faction factions)))

;(parse-factions 8)
