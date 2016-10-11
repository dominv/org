(ns org.core
  (:require
   [rum.core :as rum]
   [clojure.set :as set]))

(defn parens
  [x]
  (str "(" x ")"))

(defn sum-by
  [f coll]
  (transduce (map f) + coll))

(defn all-languages
  [repos]
  (transduce
   (map :languages)
   clojure.set/union
   repos))

(defn filter-by-language
  [lang repos]
  (filter (fn [repo]
           (contains? (:languages repo) lang))
          repos))


(rum/defcs navigation < (rum/local false :visible?)
  [{:keys [visible?]}]
  (let [is-visible? @visible?
        toggle-visibility (fn [ev]
                            (.preventDefault ev)
                            (swap! visible? not))]
    (let [menu (if is-visible?
                 :div.menu.is-visible
                 :div.menu)
          fade (if is-visible?
                 :div.menu-panel-fade-screen.is-visible
                 :div.menu-panel-fade-screen)]
      [:nav
       [:div.brand [:img {:src "img/nav-brand.png", :alt ""}]]
       [:div.panel-button
        [:span.octicon.octicon-three-bars.menu-panel-button {:on-click toggle-visibility}]]
       [menu
        [:ul
         [:li [:a {:href "#"} "How to"]]
         [:li [:a {:href "#"} "Blog"]]
         [:li [:a {:href "#"} "Contact"]]]]
       [fade {:on-click toggle-visibility}]])))

(rum/defc stats
  [repos]
  [:div.github-stats
   [:ul
    [:li.contributors
     [:span (sum-by :contributors repos)]
     [:span [:span.octicon.octicon-person] "contributors"]]
    [:li.stars
     [:span (sum-by :stars repos)]
     [:span [:span.octicon.octicon-star] "stars"]]
    [:li.repositories
     [:span (count repos)]
     [:span [:span.octicon.octicon-repo] "repositories"]]
    [:li.languages
     [:span (count (all-languages repos))]
     [:span [:span.octicon.octicon-code] "languages"]]]])

(rum/defc header
  [organization repos]
  [:header#site-header
   [:div.wrapper
    (navigation)
    [:h1 (str "Open Source Projects by " organization)]
    (stats repos)]])

(rum/defc main
  [repos]
  [:main#site-main
   [:div.wrapper
    [:div.search
     [:input
      {:type "text",
       :name "nombre",
       :placeholder "Search a project"}]]
    [:div.filter
     [:div.tag
      [:ul
       [:li [:a.active {:href "#"} "All " [:span (parens (count repos))]]]
       (for [lang (all-languages repos)]
         [:li
          {:key lang}
          [:a {:href "#"}
           lang
           [:span (parens (count (filter-by-language lang repos)))]]])]]
     [:div.order-by
      [:div.dropdown-select
       [:p.description "Order by"]
       [:p.button "Stars" [:i.fa.fa-caret-down]]
       [:ul [:li "Stars"] [:li "Forks"] [:li "Updated"]]]]]
    [:div.project-list
     (for [{:keys [name description url stars forks contributors languages]} repos]
       [:a.project-info
        {:href url
         :key name}
        [:img {:src "img/image-project-info.png", :alt ""}]
        [:h2 name]
        [:p  description]
        [:ul
         [:li [:span.octicon.octicon-code] [:span (apply str (interpose ", " languages))]]
         [:li]
         [:li [:span.octicon.octicon-git-branch] [:span forks]]
         [:li]
         [:li [:span.octicon.octicon-star] [:span stars]]
         [:li]
         [:li [:span.octicon.octicon-person] [:span contributors]]]])]]])

(rum/defc footer
  []
  [:footer#site-footer
   [:div.wrapper
    [:div.navigation
     [:ul
      [:li [:a {:href "#"} "How to"]]
      [:li [:a {:href "#"} "Blog"]]
      [:li [:a {:href "#"} "Contact"]]]
     [:p
      "Copyright © 2016 "
      [:a {:href "#"} "47 Degrees"]
      " - Reactive, scalable software solutions."]]
    [:div.social
     [:ul
      [:li "\n\t\t\t\t\t\tFollow us\n\t\t\t\t\t"]
      [:li [:a {:href ""} [:i.fa.fa-github]]]
      [:li [:a {:href ""} [:i.fa.fa-twitter]]]
      [:li [:a {:href ""} [:i.fa.fa-facebook-official]]]]]]])

(rum/defc app < rum/reactive
  [state]
  (let [{:keys [organization repos]} (rum/react state)]
    [:div
     (header organization repos)
     (main repos)
     (footer)]))
