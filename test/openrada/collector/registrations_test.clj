(ns openrada.collector.registrations-test
  (:use clojure.test)
  (:require [openrada.collector.registrations :as registrations]))

(deftest check-registrations

  (testing "Parse Parasiuk - online registrations"
    (let [registrations (registrations/parse-member-online-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")]
      (is (= (count registrations) 11))
      (is (= (:date (first registrations) "27.11.2014 12:04:53")))
      (is (= (:type (first registrations) "Ранкова реєстрація")))
      (is (= (:status (first registrations) "present")))))

  (testing "Parse Parasiuk - offline registrations"
    (let [registrations (registrations/parse-member-offline-registrations "http://w1.c1.rada.gov.ua/pls/radan_gs09/ns_dep_reg_list?startDate=27.11.2014&endDate=12.01.2015&kod=87")]
      (is (= (count registrations) 11))
      (is (= (:date (first registrations) "27.11.2014")))
      (is (= (:type (first registrations) "Ранкова реєстрація")))
      (is (= (:status (first registrations) "present")))))
  )
