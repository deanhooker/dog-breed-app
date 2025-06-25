(ns dog-breed-app.events
  "Event handling namespace."
  (:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [dog-breed-app.db :refer [app-db]]
   [dog-breed-app.types :refer [->Breed] :as t]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   app-db))

(rf/reg-event-db
 :set-view
 (fn [db [_ view breed]]
   (assoc db
          :current-view view
          :images/page 1 ;; set page to 1 when view is set
          :images/current-breed breed)))

(rf/reg-event-db
 :page/next
 (fn [db _]
   (update db :images/page inc)))

(rf/reg-event-db
 :page/previous
 (fn [db _]
   (update db :images/page #(max 1 (dec %)))))

(rf/reg-event-fx
 :fetch-breeds
 (fn [{:keys [db]} _]
   (if (or (:breeds db) (get-in db [:loading? :breeds]))
     {}
     (let [uri "https://dog.ceo/api/breeds/list/all"]
       (js/console.log "HTTP request to" uri)
       {:db (assoc-in db [:loading? :breeds] true)
        :http-xhrio {:method :get
                     :uri uri
                     :response-format (ajax/json-response-format)
                     :on-success [:fetch-breeds-success]
                     :on-failure [:http-failure]}}))))

(rf/reg-event-db
 :fetch-breeds-success
 (fn [db [_ response]]
   (let [breeds (-> (get response "message") t/parse-breeds-response)]
     (-> db
         (assoc :breeds breeds)
         (assoc-in [:loading? :breeds] false)))))

(rf/reg-event-fx
 :fetch-breed-images
 (fn [{:keys [db]} [_ breed]]
   (if (or (get-in db [:breed-images breed])
           (get-in db [:loading? :breed-images breed]))
     {}
     (let [uri (str "https://dog.ceo/api/breed/" (t/breed->path breed) "/images")]
       (js/console.log "HTTP request to" uri)
       {:db (assoc-in db [:loading? :breed-images breed] true)
        :http-xhrio {:method :get
                     :uri uri
                     :timeout 5000
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success [:fetch-breed-images-success breed]
                     :on-failure [:http-failure]}}))))

(rf/reg-event-db
 :fetch-breed-images-success
 (fn [db [_ breed response]]
   (-> db
       (assoc-in [:breed-images breed] (:message response))
       (assoc-in [:loading? :breed-images breed] false))))

(rf/reg-event-db
 :http-failure
 (fn [db [_ error]]
   (js/console.error "HTTP Error:" error)
   (assoc db :current-view :error-view)))
