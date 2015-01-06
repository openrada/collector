(ns openrada.collector.factions-test
  (:use clojure.test)
  (:require [openrada.collector.factions :as factions]))

(deftest check-parses-factions-list

  (testing "Parse factions list"
    (let [factions (factions/parse-factions-list "http://w1.c1.rada.gov.ua/pls/site2/p_fractions" 8)
          faction (first factions)]
      (is (= (count factions) 8))
      (is (= (:convocation faction) 8))
      (is (= (:faction_name faction) "Фракція ПАРТІЇ \"БЛОК ПЕТРА ПОРОШЕНКА\""))
      (is (= (:link faction) "http://w1.c1.rada.gov.ua/pls/site2/p_fraction?pidid=2613")))))


(deftest parse-faction

  (testing "Parse Samopomich"
    (let [f (factions/parse-faction {:link "http://w1.c1.rada.gov.ua/pls/site2/p_fraction?pidid=2614"
                                     :convocation 8
                                     :faction_name "Фракція Політичної партії \"Об'єднання \"САМОПОМІЧ\""})]
      (is (= (:convocation f) 8))
      (is (= (:faction_name f) "Фракція Політичної партії \"Об'єднання \"САМОПОМІЧ\""))
      (is (= (:link f) "http://w1.c1.rada.gov.ua/pls/site2/p_fraction?pidid=2614"))
      (is (= (count (:members f)) 32))
      (is (= (:member (first (:members f))) "Бабак Альона Валеріївна"))
      (is (= (:role (first (:members f))) "Член депутатської фракції")))))




