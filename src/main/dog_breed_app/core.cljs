(ns dog-breed-app.core
  (:require
   [dog-breed-app.events]
   [dog-breed-app.subscriptions]
   [dog-breed-app.views :as views]
   [re-frame.core :as rf]
   [reagent.dom :as dom]))

(defn ^:dev/after-load start []
  (dom/render
   [views/main-panel]
   (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [:initialize])
  (start))
