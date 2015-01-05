(ns openrada.collector.committees-test
  (:use clojure.test)
  (:require [openrada.collector.committees :as committees]))

(deftest check-parse-committe

  (testing "Parse Foreign Affairs"
    (let [committee (committees/parse-committee {:link "http://w1.c1.rada.gov.ua/pls/site2/p_komity?pidid=2629"
                                                 :full_name "Комітет у закордонних справах"
                                                 :convocation 8})]
      (is (= (:created committee) "2014-12-04"))
      (is (= (:convocation committee) 8))
      (is (= (:committee_name committee) "Комітет у закордонних справах"))
      (is (= (:link committee) "http://w1.c1.rada.gov.ua/pls/site2/p_komity?pidid=2629"))
      (is (= (:site committee) "http://komzak.rada.gov.ua/"))
      (is (= (count (:members committee)) 10))
      (is (= (:member (first (:members committee))) "Ар'єв Володимир Ігорович"))
      (is (= (:role (first (:members committee))) "Голова підкомітету"))
      (is (= (:member (last (:members committee))) "Тарасюк Борис Іванович"))
      (is (= (:role (last (:members committee))) "Заступник голови Комітету, голова підкомітету Комітету"))))

  (testing "Parse Budget"
    (let [committee (committees/parse-committee {:link "http://w1.c1.rada.gov.ua/pls/site2/p_komity?pidid=2622"
                                                 :full_name "Комітет з питань бюджету"
                                                 :convocation 8})]
      (is (= (:created committee) "2014-12-04"))
      (is (= (:convocation committee) 8))
      (is (= (:committee_name committee) "Комітет з питань бюджету"))
      (is (= (:link committee) "http://w1.c1.rada.gov.ua/pls/site2/p_komity?pidid=2622"))
      (is (= (:site committee) nil))
      (is (= (count (:members committee)) 28)))))


