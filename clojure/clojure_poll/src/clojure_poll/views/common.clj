(ns clojure_poll.views.common
  (:use [clojure.java.io :only [make-writer]]
        [noir.core]
        [noir.response :only [redirect]]
        [hiccup.page :only [include-css html5]]
        [hiccup.element :only [link-to]]
        [hiccup.form :only [text-field label form-to submit-button]])
  (:require [clojure_poll.models.poll :as poll]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "Clojure Poll"]
               (include-css "/css/reset.css")
               (include-css "/css/primary.css")]
              [:body
               [:div#nav
                [:ul
                 [:li (link-to "/polls/list/" "Polls")]
                 [:li (link-to "/polls/create/" "Create a Poll")]]]
               [:div.header-block
                [:h1.header "Clojure Poll"]]
               [:div#wrapper
                content]]))

(defpartial polls-list [polls]
            (for [poll polls]
              [:pre.poll-title (link-to (str "/poll/" (:id poll) "/") (:title poll)) (str \space (count (:votes poll)) " votes")]))

(defpartial poll-show [poll]
            [:h2.poll-title (:title poll)
             (let [options (:options poll) votes (:votes poll)]
               [:div.options
                (for [opt-vote-pair (->> (interleave options (partition-by :option_id votes))
                                      (partition 2))
                      :let [[option opt-votes] opt-vote-pair]]
                  [:div.option
                   [:span.option-text (:text option)]
                   [:span.option-count (count opt-votes)]])])])


(defpartial poll-create-form []
            (label "title" "Title: ")
            (text-field "title")
            (label "option" "Options (separate with commas): ")
            (text-field "options"))

(defpage "/polls/list/" []
         (layout
           (polls-list (map deref @poll/polls))))

(defpage [:get ["/poll/:id/" :id #"\d+"]]  {:keys [id]}
         (try
           (let [id (Integer. id)]
             (layout
               (if-let [poll (poll/get-poll :id id)]
                 (poll-show poll)
               [:span.error "That poll does not exist"])))
         (catch NumberFormatException n (str "Invalid ID format: " (.getMessage n)))))
         ;(if id
         ;  (let [matches (filter #(= (:id %) (Integer/parseInt id)) @poll/polls )]
         ;    (if (empty? matches)
         ;      [:span.error "Poll with that ID does not exist"]
         ;      (poll-list @(first matches))))))

(defpage [:get "/polls/create/"] []
         (layout
           (form-to [:post "/polls/create/"]
                    (poll-create-form)
                    (submit-button "Create"))))

(defpage [:post "/polls/create/"] {:keys [title options]}
         (let [options (clojure.string/split options #",*\s*,*")]
           (if (poll/get-poll :title title)
             (layout
               [:span.error "Poll with that title already exists!"])
             (do
               (poll/add-poll! title options)
               (redirect "/polls/list/")))))

