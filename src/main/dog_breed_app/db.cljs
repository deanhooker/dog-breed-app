(ns dog-breed-app.db
  "Initial application state.")

(def app-db
  {:current-view :breeds-view ; initialize with breeds-view
   :breeds nil
   :breed-images {}
   :images/current-breed nil
   :images/page 1
   :images/page-size 20
   :loading? {:breeds false
              :breed-images {}}})
