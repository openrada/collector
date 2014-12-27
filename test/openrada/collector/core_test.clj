(ns openrada.collector.core-test
  (:use clojure.test)
  (:require [openrada.collector.core :as collector]))

(deftest check-parse-member

  (testing "Parse Parasiuk"
    (let [member (collector/parse-member "http://gapp.rada.gov.ua/mps/info/page/18124")]
      (is (= (:member_since member) "2014-11-27"))
      (is (= (:dob member) "1987-07-9"))
      (is (= (:email member) "Parasiuk.Volodymyr@rada.gov.ua"))
      (is (= (:fraction member) "Не входить до складу будь-якої фракції"))
      (is (= (:district member) "Виборчий округ №122"))
      (is (= (:region member) "Львівська область"))
      (is (= (:title (:roles member)) "Член Комітету Верховної Ради України з питань запобігання і протидії корупції"))))


  (testing "Parse Yarosh"
    (let [member (collector/parse-member "http://gapp.rada.gov.ua/mps/info/page/18153")]
      (is (= (:member_since member) "2014-11-27"))
      (is (= (:dob member) "1971-09-30"))
      (is (= (:email member) "Yarosh.Dmytro@rada.gov.ua"))
      (is (= (:fraction member) "Не входить до складу будь-якої фракції"))
      (is (= (:district member) "Виборчий округ №39"))
      (is (= (:region member) "Дніпропетровська область"))
      (is (= (:title (:roles member)) "Заступник голови Комітету Верховної Ради України з питань національної безпеки і оборони")))))


(deftest check-parse-members-for-rada-8

  (testing "Parse All Links"
    (let [members (collector/parse-members-8)
          abdullin (first members)]
      (is (= (count members) 422))
      (is (= (:convocation abdullin) 8))
      (is (= (:link abdullin) "http://gapp.rada.gov.ua/mps/info/page/2524"))
      (is (= (:full_name abdullin) "Абдуллін Олександр Рафкатович"))
      (is (= (:short_name abdullin) "Абдуллін О.Р.")))))

