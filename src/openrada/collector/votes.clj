(ns openrada.collector.votes
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [cuerdas.core :as str]
            [openrada.collector.utils :as utils]))


;http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_pd1
(defn parse-voting-days [page-url convocation]
  (let [page (utils/fetch-url page-url)
        links-els (html/select page [(html/attr= :style "background-color:#FFFFAE;") :a])]
                   (utils/get-links links-els)
                   ))

(parse-voting-days "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_pd1" 8)
