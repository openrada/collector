(ns openrada.collector.core-test
  (:use clojure.test)
  (:require [openrada.collector.core :as collector]))

(deftest check-parse-deputy

  (testing "Parse Parasiuk"
    (let [deputy (collector/parse-deputy "http://gapp.rada.gov.ua/mps/info/page/18124")]
      (is (= (:deputy_since deputy) "2014-11-27"))
      (is (= (:dob deputy) "1987-07-9"))
      (is (= (:email deputy) "Parasiuk.Volodymyr@rada.gov.ua"))
      (is (= (:fraction deputy) "Не входить до складу будь-якої фракції"))
      (is (= (:district deputy) "Виборчий округ №122"))
      (is (= (:region deputy) "Львівська область"))
      (is (= (:title (:roles deputy)) "Член Комітету Верховної Ради України з питань запобігання і протидії корупції"))))


  (testing "Parse Yarosh"
    (let [deputy (collector/parse-deputy "http://gapp.rada.gov.ua/mps/info/page/18153")]
      (is (= (:deputy_since deputy) "2014-11-27"))
      (is (= (:dob deputy) "1971-09-30"))
      (is (= (:email deputy) "Yarosh.Dmytro@rada.gov.ua"))
      (is (= (:fraction deputy) "Не входить до складу будь-якої фракції"))
      (is (= (:district deputy) "Виборчий округ №39"))
      (is (= (:region deputy) "Дніпропетровська область"))
      (is (= (:title (:roles deputy)) "Заступник голови Комітету Верховної Ради України з питань національної безпеки і оборони")))))


(deftest check-parse-deputies-for-rada-8

  (testing "Parse All Links"
    (let [deputies (collector/parse-deputies-8)
          abdullin (first deputies)]
      (is (= (count deputies) 422))
      (is (= (:rada abdullin) 8))
      (is (= (:link abdullin) "http://gapp.rada.gov.ua/mps/info/page/2524"))
      (is (= (:full_name abdullin) "Абдуллін Олександр Рафкатович"))
      (is (= (:short_name abdullin) "Абдуллін О.Р.")))))

