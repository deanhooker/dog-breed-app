(ns dog-breed-app.views
  (:require
   [re-frame.core :as rf]
   [reagent.core :as r]))

(defn loading-spinner []
  [:div "Loading..."])

(defn breeds-view []
  (rf/dispatch [:fetch-breeds])
  (let [breeds @(rf/subscribe [:breeds])
        loading? @(rf/subscribe [:loading? :breeds])]
    [:div
     [:h2 "Dog Breeds"]
     (if loading?
       [loading-spinner]
       [:ul
        (for [breed breeds]
          ^{:key (str (:primary-breed breed) "-" (:sub-breed breed))}
          [:li [:a {:href "#"
                    :on-click #(rf/dispatch [:change-view :breed-images-view breed])}
                (str (:sub-breed breed) " " (:primary-breed breed))]])])]))

(defn breed-images-view []
  (let [breed @(rf/subscribe [:images/current-breed])
        _ (rf/dispatch [:fetch-breed-images breed])
        images @(rf/subscribe [:breed-images breed])
        paged-images @(rf/subscribe [:images/paged images])
        page @(rf/subscribe [:images/current-page])
        has-next? @(rf/subscribe [:images/has-next? breed])
        loading? @(rf/subscribe [:loading? :breed-images breed])]
    (if (or (nil? loading?) loading?)
      [loading-spinner]
      [:div
       [:h2 "Images of " (:sub-breed breed) " " (:primary-breed breed)]
       [:button {:on-click #(rf/dispatch [:change-view :breeds-view])}
        "<- Back to breeds"]
       [:p (str "Total images: " (count images))]
       [:div {:style {:display "grid"
                      :grid-template-columns "repeat(auto-fill, minmax(150px, 1fr))"
                      :gap "1em"}}
        (for [url paged-images]
          ^{:key url} [:img {:src url :style {:max-width "100%"}}])]
       [:div {:style {:margin "0 1em"}}
        [:button {:on-click #(rf/dispatch [:page/previous breed])
                  :disabled (= 1 page)} "Previous"]
        [:span {:style {:margin "0 1em"}} (str "Page " page)]
        [:button {:on-click #(rf/dispatch [:page/next breed])
                  :disabled (not has-next?)} "Next"]]])))

(defn error-view []
  [:div "Error retrieving breed information, contact support or restart app."])

(defn main-panel []
  (let [current-page @(rf/subscribe [:current-view])]
    (case current-page
      :breeds-view [breeds-view]
      :breed-images-view [breed-images-view]
      :error-view [error-view]
      [:div "Unknown page"])))
