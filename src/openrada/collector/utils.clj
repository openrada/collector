(ns openrada.collector.utils
  (:require [clj-http.client :as http-client]))


;; Import Apache Common's Base64 encoder/decoder
(import (org.apache.commons.codec.binary Base64))


(defn fetch-image-as-base64 [url]
  (-> (http-client/get url {:as :byte-array})
      :body
      (Base64/encodeBase64)
      (String.)))
