(ns imjur-js.core
  (:require [goog.events :as gevents]
            [goog.dom :as gdom]
            ))

(def upload-area (.querySelector js/document ".upload-area"))

(defn on-hover
  [e]
  (gdom/setProperties upload-area #js {"class" "highlight"})) 


(defn main
  []
  (println upload-area)
  (gevents/listen upload-area "keyup" 'on-hover))

(.addEventListener
  js/window
  "DOMContentLoaded"
  (fn [] (main)))
