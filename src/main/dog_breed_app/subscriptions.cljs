(ns dog-breed-app.subscriptions
  "Subscription handling namespace."
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :breeds
 (fn [db _]
   (:breeds db)))

(rf/reg-sub
 :breed-images
 (fn [db [_ breed]]
   (get-in db [:breed-images breed])))

(rf/reg-sub
 :current-view
 (fn [db _]
   (:current-view db)))

(rf/reg-sub
 :images/current-breed
 (fn [db _]
   (:images/current-breed db)))

(rf/reg-sub
 :images/current-page
 (fn [db _]
   (:images/page db)))

(rf/reg-sub
 :images/has-next?
 (fn [{:keys [images/page images/page-size breed-images]} [_ breed]]
   (< (* page page-size) (count (get breed-images breed)))))

(rf/reg-sub
 :images/paged
 (fn [{:keys [images/page images/page-size breed-images]} [_ breed]]
   (when-let [images (get breed-images breed)]
     (let [start (* (dec page) page-size)
           end (min (count images) (+ start page-size))]
       (subvec images start end)))))

(rf/reg-sub
 :loading?
 (fn [db [_ & keys]]
   (get-in (:loading? db) keys)))
