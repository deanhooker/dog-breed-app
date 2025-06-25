(ns dog-breed-app.core
  "Entry point for the application UI."
  (:require
   [dog-breed-app.events]
   [dog-breed-app.subscriptions]
   [dog-breed-app.views :as views]
   [re-frame.core :as rf]
   [reagent.dom :as dom]))

;; `init` is called once on app startup.
;; `start` is called on code reload to re-render UI during dev

(defn ^:dev/after-load start []
  (dom/render
   [views/main-panel]
   (.getElementById js/document "app")))

(defn ^:export init []
  (js/console.log "Initializing app")
  (rf/dispatch-sync [:initialize])
  (start))
