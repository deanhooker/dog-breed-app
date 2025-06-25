(ns dog-breed-app.db
  "Initial application state.")

(def app-db
  {
   ;; View state
   :current-view :breeds-view ; initialize with breeds-view

   ;; Data
   :breeds nil
   :breed-images {}

   ;; Pagination
   :images/current-breed nil
   :images/page 1
   :images/page-size 20

   ;; Loading Flags
   :loading? {:breeds false
              :breed-images {}}

   })
