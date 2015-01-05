(ns openrada.collector.utils
  (:require [clj-http.client :as http-client]
            [net.cgrand.enlive-html :as html]
            [cuerdas.core :as str]))


;; Import Apache Common's Base64 encoder/decoder
(import (org.apache.commons.codec.binary Base64))


(defn fetch-image-as-base64 [url]
  (-> (http-client/get url {:as :byte-array})
      :body
      (Base64/encodeBase64)
      (String.)))


(defn fetch-url
  ([url] (fetch-url url "windows-1251"))
  ([url encoding]
  (-> url
      java.net.URL.
      .getContent (java.io.InputStreamReader. encoding) ;<- encoding goes here
      html/html-resource)))



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
        tokens (str/split date-str " ")
        year (str/join (take 4 (last tokens)))
        month (transform-month (second tokens))
        day (first tokens)]
    (str year "-" month "-" day)))
