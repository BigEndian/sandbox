(ns clojure_poll.views.welcome
  (:require [clojure_poll.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to clojure_poll"]))
