(ns openrada.collector.registrations-test
  (:use clojure.test)
  (:require [openrada.collector.registrations :as registrations]
            [clj-time.core :as t]))


(deftest check-registrations

  (testing "Parse Parasiuk - online registrations"
    (let [registrations
          (registrations/parse-member-online-registrations
           "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")]
      (is (= (count registrations) 11))
      (is (= (:date (first registrations) "27.11.2014 12:04:53")))
      (is (= (:type (first registrations) "Ранкова реєстрація")))
      (is (= (:ref_type (first registrations) "online")))
      (is (= (:status (first registrations) "present")))))

  (testing "Parse Parasiuk - offline registrations"
    (let [registrations
          (registrations/parse-member-offline-registrations
           "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")]
      (is (= (count registrations) 11))
      (is (= (:date (first registrations) "27.11.2014")))
      (is (= (:type (first registrations) "Ранкова реєстрація")))
      (is (= (:ref_type (first registrations) "offline")))
      (is (= (:status (first registrations) "present")))))

  (testing "Parse Parasiuk - online registrations from original link"
    (let [registrations
          (registrations/parse-member-online-registrations
           "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep?vid=2&kod=87"
           (t/date-time 2014 11 27))]
      (is (= (count registrations) 17))
      (is (= (:date (first registrations) "27.11.2014 12:04:53")))
      (is (= (:type (first registrations) "Ранкова реєстрація")))
      (is (= (:ref_type (first registrations) "online")))
      (is (= (:status (first registrations) "present")))))

  (testing "Parse Parasiuk - offline registrations from original link"
    (let [registrations
          (registrations/parse-member-offline-registrations
           "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep?vid=3&kod=87"
           (t/date-time 2014 11 27))]
      (is (= (count registrations) 16))
      (is (= (:date (first registrations) "27.11.2014")))
      (is (= (:type (first registrations) "Ранкова реєстрація")))
      (is (= (:ref_type (first registrations) "offline")))
      (is (= (:status (first registrations) "present")))))


  (testing "Parse Pysarenko online registration"
    (let [registrations
          (registrations/parse-member-offline-registrations
           "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep?vid=2&kod=129"
           (t/date-time 2014 11 27))]
      (is (= (count registrations) 16))
      (is (= (:date (second registrations) "27.11.2014")))
      (is (= (:type (second registrations) "Ранкова реєстрація")))
      (is (= (:ref_type (second registrations) "online")))
      (is (= (:status (second registrations) "absent"))))))
