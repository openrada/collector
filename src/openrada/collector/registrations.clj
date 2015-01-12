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

(defn parse-member-online-registrations
  "example page-url
  http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87"
  [page-url]
  (let [page (utils/fetch-url page-url)
        rows (map #(html/text %) (html/select page [:ul.pd :li]))]
    (map (fn [row]
           (let [clean-row (filter #(not (str/blank? %)) (map str/trim(str/lines row)))]
             {:date (str/clean (nth clean-row 1))
              :type (nth clean-row 2)
              :status (transform-offline-status(last clean-row))}
             )
           ) rows)))
;(parse-member-online-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")


(defn parse-member-offline-registrations
  "example page-url
  http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_w_list?startDate=27.11.2014&endDate=12.01.2015&kod=87"
  [page-url]
  (let [page (utils/fetch-url page-url)
        rows (map #(html/text %) (html/select page [:ul.pd :li]))]
    (map (fn [row]
           (let [clean-row (filter #(not (str/blank? %)) (map str/trim(str/lines row)))]
             {:date (str/trim (nth clean-row 2))
              :type (nth clean-row 3)
              :status (transform-online-status(last clean-row))}
             )
           ) rows)))

;(parse-member-offline-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_w_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")
