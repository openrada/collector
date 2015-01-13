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



(defn build-online-regs-url [url date]
  (let [s1 (str/replace url "ns_dep" "ns_dep_reg_list")
        s2 (str/replace s1 "vid=2&" "")
        start-date (utils/to-ua-date-str date)
        end-date (utils/to-ua-date-str)]
      (str s2 "&startDate=" start-date "&endDate=" end-date)))

(defn build-offline-regs-url [url date]
  (let [s1 (str/replace url "ns_dep" "ns_dep_reg_w_list")
        s2 (str/replace s1 "vid=3&" "")
        start-date (utils/to-ua-date-str date)
        end-date (utils/to-ua-date-str)]
      (str s2 "&startDate=" start-date "&endDate=" end-date)))

(defn parse-member-online-registrations
  "example page-url
  http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87"
  ([url start-date] (parse-member-online-registrations (build-online-regs-url url start-date)))
  ([page-url]
  (let [page (utils/fetch-url page-url)
        rows (map #(html/text %) (html/select page [:ul.pd :li]))]
    (map (fn [row]
           (let [clean-row (filter #(not (str/blank? %)) (map str/trim(str/lines row)))]
             {:date (str/clean (nth clean-row 1))
              :type (nth clean-row 2)
              :status (transform-offline-status(last clean-row))}
             )
           ) rows))))
;(parse-member-online-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")


(defn parse-member-offline-registrations
  "example page-url
  http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_w_list?startDate=27.11.2014&endDate=12.01.2015&kod=87"
  ([url start-date] (parse-member-offline-registrations (build-offline-regs-url url start-date)))
  ([page-url]
  (let [page (utils/fetch-url page-url)
        rows (map #(html/text %) (html/select page [:ul.pd :li]))]
    (map (fn [row]
           (let [clean-row (filter #(not (str/blank? %)) (map str/trim(str/lines row)))]
             {:date (str/trim (nth clean-row 2))
              :type (nth clean-row 3)
              :status (transform-online-status(last clean-row))}
             )
           ) rows))))

;(parse-member-offline-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_w_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")
