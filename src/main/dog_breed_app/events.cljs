(ns dog-breed-app.events
  (:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [dog-breed-app.db :refer [app-db]]
   [dog-breed-app.types :refer [->Breed]]
   [re-frame.core :as rf]))

;; Initialize App
;; ==================================================================
(rf/reg-event-db
 :initialize
 (fn [_ _]
   app-db))

;; Navigate
;; ==================================================================
(rf/reg-event-db
 :change-view
 (fn [db [_ view breed]]
   (assoc db
          :current-view view
          :images/page 1 ;; set page to 1 when view changes
          :images/current-breed breed)))

(rf/reg-event-db
 :page/next
 (fn [db [_ breed]]
   (update db :images/page inc)))

(rf/reg-event-db
 :page/previous
 (fn [db [_ breed]]
   (update db :images/page #(max 1 (dec %)))))

;; Fetch Breeds
;; ==================================================================
(rf/reg-event-fx
 :fetch-breeds
 (fn [{:keys [db]} _]
   (if (or (:breeds db) (get-in db [:loading? :breeds]))
     {}
     {:db (assoc-in db [:loading? :breeds] true)
      :http-xhrio {:method :get
                   :uri "https://dog.ceo/api/breeds/list/all"
                   :response-format (ajax/json-response-format)
                   :on-success [:fetch-breeds-success]
                   :on-failure [:http-failure]}})))

(defn parse-breeds [breeds-map]
  (->> breeds-map
       (map (fn [[primary-breed sub-breeds]]
              (if (seq sub-breeds)
                (map #(->Breed primary-breed %) sub-breeds)
                (->Breed primary-breed nil))))
       flatten
       (sort-by (juxt :primary-breed :sub-breed))))

(rf/reg-event-db
 :fetch-breeds-success
 (fn [db [_ response]]
   (let [breeds (-> (get response "message") parse-breeds)]
     (-> db
         (assoc :breeds breeds)
         (assoc-in [:loading? :breeds] false)))))

;; Fetch Breed Images
;; ==================================================================
(defn breed->path [^Breed breed]
  (if-let [sub (:sub-breed breed)]
    (str (:primary-breed breed) "/" (:sub-breed breed))
    (:primary-breed breed)))

(rf/reg-event-fx
 :fetch-breed-images
 (fn [{:keys [db]} [_ breed]]
   (if (or (get-in db [:breed-images breed])
           (get-in db [:loading? :breed-images breed]))
     {}
     {:db (assoc-in db [:loading? :breed-images breed] true)
      :http-xhrio {:method :get
                   :uri (str "https://dog.ceo/api/breed/" (breed->path breed) "/images")
                   :timeout 5000
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success [:fetch-breed-images-success breed]
                   :on-failure [:http-failure]}})))

(rf/reg-event-db
 :fetch-breed-images-success
 (fn [db [_ breed response]]
   (-> db
       (assoc-in [:breed-images breed] (:message response))
       (assoc-in [:loading? :breed-images breed] false))))

;; HTTP Error
;; ==================================================================
(rf/reg-event-db
 :http-failure
 (fn [db _]
   (assoc db :current-view :error-view)))
