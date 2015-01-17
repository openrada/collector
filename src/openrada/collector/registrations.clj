(ns openrada.collector.registrations
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [cuerdas.core :as str]
            [openrada.collector.utils :as utils]))



(defn transform-online-status [status]
  (case status
    "Присутній" "present"
    "Відсутній" "absent"
    nil))

(defn transform-offline-status [status]
  (case status
    "Зареєстрований" "present"
    "Незареєстрований" "absent"
    nil))

(defn transform-online-type [rtype]
  (case rtype
    "Ранкова реєстрація" "morning"
    "Вечірня реєстрація" "evening"
    nil))

(defn transform-offline-type [rtype]
  (case rtype
    "Ранкове засідання" "morning"
    "Вечірнє засідання" "evening"
    nil))

(defn cleanup-online-url [url]
  (if (str/contains? url "ns_dep_reg_list")
    url
    (let [s1 (str/replace url "ns_dep" "ns_dep_reg_list")
          s2 (str/replace s1 "vid=2&" "")]
      s2)))

(cleanup-online-url
 "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")

(defn build-online-regs-url [url date]
  (let [start-date (utils/to-ua-date-str date)
        end-date (utils/to-ua-date-str)]
      (str url "&startDate=" start-date "&endDate=" end-date)))

(defn cleanup-offline-url [url]
  (if (str/contains? url "ns_dep_reg_w_list")
    url
    (let [s1 (str/replace url "ns_dep" "ns_dep_reg_w_list")
          s2 (str/replace s1 "vid=3&" "")]
      s2)))

(defn build-offline-regs-url [url date]
  (let [start-date (utils/to-ua-date-str date)
        end-date (utils/to-ua-date-str)]
      (str url "&startDate=" start-date "&endDate=" end-date)))

(str/clean "23.12.2014    10:09:19")

(defn parse-member-online-registrations
  "example page-url
  http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87"
  ([url start-date] (parse-member-online-registrations (build-online-regs-url url start-date)))
  ([page-url]
    (let [url  (cleanup-online-url page-url)
          page (utils/fetch-url url)
          rows (map #(html/text %) (html/select page [:ul.pd :li]))]
      (map (fn [row]
             (let [clean-row (filter #(not (str/blank? %)) (map str/trim (str/lines row)))]
               {:date (str/clean (nth clean-row 1))
                :type (transform-online-type (nth clean-row 2))
                :status (transform-offline-status (last clean-row))
                :reg_type "online"})
             ) rows))))
;(parse-member-online-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")


(defn parse-member-offline-registrations
  "example page-url
  http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_w_list?startDate=27.11.2014&endDate=12.01.2015&kod=87"
  ([url start-date] (parse-member-offline-registrations (build-offline-regs-url url start-date)))
  ([page-url]
    (let [url  (cleanup-offline-url page-url)
          page (utils/fetch-url url)
          rows (map #(html/text %) (html/select page [:ul.pd :li]))]
      (map (fn [row]
             (let [clean-row (filter #(not (str/blank? %)) (map str/trim(str/lines row)))]
               {:date (str/trim (nth clean-row 2))
                :type (transform-offline-type (nth clean-row 3))
                :status (transform-online-status (last clean-row))
                :reg_type "offline"})
             ) rows))))

;(parse-member-offline-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_w_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")
