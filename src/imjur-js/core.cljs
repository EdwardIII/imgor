(ns imjur-js.core
  (:require [goog.events :as gevents]
            [goog.dom :as gdom]
            [goog.dom.classlist :as gcss]))

(defn get-upload-area [] (gdom/getElementByClass "upload-area"))

(defn on-hover
  [e]
  (gcss/toggle (get-upload-area) "highlight"))


(defn main
  []
  (gevents/listen (get-upload-area) "mouseenter" on-hover)
  (gevents/listen (get-upload-area) "mouseleave" on-hover))

(.addEventListener
  js/window
  "DOMContentLoaded"
  (fn [] (main)))
