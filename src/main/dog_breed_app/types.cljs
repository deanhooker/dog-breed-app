(ns dog-breed-app.types)

(defrecord Breed [primary-breed sub-breed])

(defn parse-breeds-response
  "Parses the response from dog.ceo listing all breeds into a sequence
  of Breed records for use in the application."
  [breeds-map]
  (->> breeds-map
       (map (fn [[primary-breed sub-breeds]]
              (if (seq sub-breeds)
                (map #(->Breed primary-breed %) sub-breeds)
                (->Breed primary-breed nil))))
       flatten
       (sort-by (juxt :primary-breed :sub-breed))))

(defn breed->path
  "Converts a Breed record into a URI path for the Dog API."
  [^Breed breed]
  (if-let [sub (:sub-breed breed)]
    (str (:primary-breed breed) "/" (:sub-breed breed))
    (:primary-breed breed)))
