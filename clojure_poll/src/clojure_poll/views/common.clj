(ns clojure_poll.views.common
  (:use [noir.core]
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
               [:div.header-block
                [:h1.header "Clojure Poll"]]
               [:div#wrapper
                content]]))

(defpartial polls-list [polls]
            (for [poll polls]
              [:pre.poll-title (link-to (str "/polls/list/" (:id poll)) (:title poll)) (str \space (count (:votes poll)) " votes")]))

(defpartial poll-list [poll]
            [[:h2.poll-title (:title poll)]
             (for [vote-opt-pair (map #(vector %1 %2)
                                       (:options poll)
                                       (partition-by :id (:votes poll)))]
               [:div.option
                [:span.option-text (:text (first vote-opt-pair))]
                [:span.option-vote-count (count (second vote-opt-pair))]])])

(defpartial poll-create-form []
            (label "title" "Title: ")
            (text-field "title")
            (label "option" "Options (separate with commas): ")
            (text-field "options"))

(defpage "/polls/list/" []
         (layout
           (polls-list (map deref @poll/polls))))

(defpage "/polls/list/:id" {:keys [id]}
         (if id
           (let [matches (filter #(= (:id %) (Integer/parseInt id)) @poll/polls )]
             (if (empty? matches)
               [:span.error "Poll with that ID does not exist"]
               (poll-list @(first matches))))))

(defpage [:get "/polls/create/"] []
         (layout
           (form-to [:post "/polls/create/"]
                    (poll-create-form)
                    (submit-button "Create"))))

(defpage [:post "/polls/create/"] {:keys [title options]}
         (let [options (clojure.string/split options #",*\s*,*")]
           (if (poll/poll-exists? title)
             (layout
               [:span.error "Poll with that title already exists!"])
             (do
               (poll/add-poll! title options)
               (redirect "/polls/list/")))))

