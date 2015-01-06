(ns openrada.collector.members-test
  (:use clojure.test)
  (:require [openrada.collector.members :as members]))

(deftest check-parse-member

  (testing "Parse Parasiuk"
    (let [member (members/parse-member "http://gapp.rada.gov.ua/mps/info/page/18124")]
      (is (= (:member_since member) "2014-11-27"))
      (is (= (:dob member) "1987-07-9"))
      (is (= (:email member) "Parasiuk.Volodymyr@rada.gov.ua"))
      (is (= (:faction member) "Не входить до складу будь-якої фракції"))
      (is (= (:district member) "Виборчий округ №122"))
      (is (= (:region member) "Львівська область"))
      (is (= (first (:notes member)) "освіта загальна середня"))
      (is (= (last (:notes member)) "самовисування"))))


  (testing "Parse Krulko"
    (let [member (members/parse-member "http://gapp.rada.gov.ua/mps/info/page/6073")]
      (is (= (:member_since member) "2014-11-27"))
      (is (= (:dob member) "1981-07-20"))
      (is (= (:email member) "Krulko.Ivan@rada.gov.ua"))
      (is (= (:phone member) "(044) 255-45-55"))
      (is (= (:party member) "політична партія Всеукраїнське об'єднання \"Батьківщина\""))
      (is (= (:rank_in_party member) "8"))))

  (testing "Parse Groisman"
    (let [member (members/parse-member "http://gapp.rada.gov.ua/mps/info/page/17973")]
      (is (= (:member_since member) "2014-11-27"))
      (is (= (:dob member) "1978-01-20"))
      (is (= (:faction member) ""))
      (is (= (:email member) "V.Groysman@rada.gov.ua"))
      (is (= (:party member) "ПАРТІЯ \"БЛОК ПЕТРА ПОРОШЕНКА\""))
      (is (= (:rank_in_party member) "4"))
      (is (= (:role member) "Голова Верховної Ради України"))))

  (testing "Parse Yarosh"
    (let [member (members/parse-member "http://gapp.rada.gov.ua/mps/info/page/18153")]
      (is (= (:member_since member) "2014-11-27"))
      (is (= (:dob member) "1971-09-30"))
      (is (= (:email member) "Yarosh.Dmytro@rada.gov.ua"))
      (is (= (:faction member) "Не входить до складу будь-якої фракції"))
      (is (= (:district member) "Виборчий округ №39"))
      (is (= (:region member) "Дніпропетровська область"))
      (is (= (first (:notes member)) "освіта вища"))
      (is (= (last (:notes member)) "суб’єкт висування – Політична партія \"ПРАВИЙ СЕКТОР\"")))))


(deftest check-parse-members-for-rada-8

  (testing "Parse All Links"
    (let [members (members/parse-members-8)
          abdullin (first members)]
      (is (= (count members) 422))
      (is (= (:convocation abdullin) 8))
      (is (= (:link abdullin) "http://gapp.rada.gov.ua/mps/info/page/2524"))
      (is (= (:full_name abdullin) "Абдуллін Олександр Рафкатович"))
      (is (= (:short_name abdullin) "Абдуллін О.Р.")))))

