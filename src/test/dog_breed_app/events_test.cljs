(ns dog-breed-app.events-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [re-frame.core :as rf]
   [re-frame.db :as db]
   [day8.re-frame.test :refer [run-test-sync]]
   [dog-breed-app.events]
   [dog-breed-app.db :refer [app-db]]
   [dog-breed-app.types :as t]))

(deftest initialize-event-test
  (run-test-sync
    (rf/dispatch-sync [:initialize])
    (is (= @db/app-db app-db))))

(deftest set-view-event-test
  (run-test-sync
    (rf/dispatch-sync [:initialize])
    (rf/dispatch [:set-view :breed-images-view (t/->Breed "bulldog" nil)])
    (let [db-state @db/app-db]
      (is (= :breed-images-view (:current-view db-state)))
      (is (= 1 (:images/page db-state)))
      (is (= (t/->Breed "bulldog" nil) (:images/current-breed db-state))))))

(deftest page-next-previous-test
  (run-test-sync
    (rf/dispatch-sync [:initialize])
    (rf/dispatch [:set-view :breed-images-view (t/->Breed "bulldog" nil)])
    (rf/dispatch [:page/next])
    (is (= 2 (:images/page @db/app-db)))
    (rf/dispatch [:page/previous])
    (is (= 1 (:images/page @db/app-db)))
    ;; Cannot go below 1
    (rf/dispatch [:page/previous])
    (is (= 1 (:images/page @db/app-db)))))

;; TODO: Tests for:
;; fetch-breeds
;; fetch-breeds-success
;; fetch-breed-images
;; fetch-breed-images-success
;; http-failure
