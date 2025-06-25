(ns dog-breed-app.types-test
  (:require
   [cljs.test :refer [deftest is testing]]
   [dog-breed-app.types :as t]))

(deftest parse-breeds-response-test
  (testing "parses breeds with sub-breeds"
    (let [input {"bulldog" ["english" "french"]
                 "retriever" ["golden"]
                 "pug" []}
          result (t/parse-breeds-response input)]
      (is (= result
             [(t/->Breed "bulldog" "english")
              (t/->Breed "bulldog" "french")
              (t/->Breed "pug" nil)
              (t/->Breed "retriever" "golden")]))))

  (testing "parses breeds with no sub-breeds"
    (let [input {"beagle" [] "husky" []}
          result (t/parse-breeds-response input)]
      (is (= result [(t/->Breed "beagle" nil)
                     (t/->Breed "husky" nil)])))))

(deftest breed->path-test
  (testing "converts breed with sub-breed"
    (let [breed (t/->Breed "bulldog" "english")]
      (is (= "bulldog/english" (t/breed->path breed)))))

  (testing "converts breed with no sub-breed"
    (let [breed (t/->Breed "pug" nil)]
      (is (= "pug" (t/breed->path breed))))))
