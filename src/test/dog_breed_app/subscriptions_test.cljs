(ns dog-breed-app.subscriptions-test
  (:require
   [cljs.test :refer [deftest is testing use-fixtures]]
   [re-frame.core :as rf]
   [dog-breed-app.subscriptions]
   [dog-breed-app.db :refer [app-db]]))

;; Setup and teardown for each test
(use-fixtures :each
  {:before (fn []
             (rf/clear-subscription-cache!)
             (rf/dispatch-sync [:initialize]))})

(deftest breeds-subscription-test
  (testing "Subscribes to :breeds"
    (let [breeds {"bulldog" []}
          _ (rf/dispatch-sync [:fetch-breeds-success {"message" breeds}])
          sub @(rf/subscribe [:breeds])]
      (is (= 1 (count sub)))
      (is (= "bulldog" (-> sub first :primary-breed))))))

(deftest breed-images-subscription-test
  (testing "Subscribes to :breed-images"
    (let [breed {:primary-breed "bulldog" :sub-breed nil}
          images ["url1" "url2"]
          _ (rf/dispatch-sync [:fetch-breed-images-success breed {:message images}])
          sub @(rf/subscribe [:breed-images breed])]
      (is (= images sub)))))

(deftest current-view-subscription-test
  (testing "Subscribes to :current-view"
    (rf/dispatch-sync [:set-view :details-view nil])
    (is (= :details-view @(rf/subscribe [:current-view])))))

(deftest current-page-subscription-test
  (testing "Subscribes to :images/current-page"
    (rf/dispatch-sync [:page/next])
    (is (= 2 @(rf/subscribe [:images/current-page])))))

(deftest has-next-subscription-test
  (testing "Correctly determines if more pages exist"
    (let [breed {:primary-breed "poodle" :sub-breed nil}
          images (vec (repeat 45 "img"))
          _ (rf/dispatch-sync [:fetch-breed-images-success breed {:message images}])]
      ;; set to page 2 (images 20-39 of 45), expect true
      (rf/dispatch-sync [:page/next])
      (is (= true @(rf/subscribe [:images/has-next? breed])))
      ;; set to page 3 (images 40-59), only 45 total — expect false
      (rf/dispatch-sync [:page/next])
      (is (= false @(rf/subscribe [:images/has-next? breed]))))))

(deftest paged-images-subscription-test
  (testing "Returns correct page of images"
    (let [breed {:primary-breed "terrier" :sub-breed nil}
          images (vec (map #(str "img" %) (range 1 41))) ;; 40 images
          _ (rf/dispatch-sync [:fetch-breed-images-success breed {:message images}])]
      ;; First page
      (is (= (subvec images 0 20)
             @(rf/subscribe [:images/paged breed])))
      ;; Second page
      (rf/dispatch-sync [:page/next])
      (is (= (subvec images 20 40)
             @(rf/subscribe [:images/paged breed]))))))

(deftest loading?-subscription-test
  (testing "Returns loading state for breeds and breed-images"
    (let [breed {:primary-breed "akita" :sub-breed nil}]
      ;; manually simulate loading state
      (rf/dispatch-sync [:fetch-breeds])
      (rf/dispatch-sync [:fetch-breed-images breed])
      (is (true? @(rf/subscribe [:loading? :breeds])))
      (is (true? @(rf/subscribe [:loading? :breed-images breed]))))))
