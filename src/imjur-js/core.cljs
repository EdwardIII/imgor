(ns imjur-js.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.events :as gevents]
            [goog.dom.classlist :as gcss]

            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def upload (atom {:file nil
                   :csrf-token nil}))
(defn uploader
  [_ _ _ upload-request]
  (go
    (let [response (<! (http/post "/"
                                  {:with-credentials? false
                                   :multipart-params [["__anti-forgery-token" (upload-request :csrf-token)]
                                                      ["file" (upload-request :file)]]}))])))
(add-watch upload :file uploader)

(defn by-css
  [selector]
  "Get single dom element by selector"
  (.querySelector js/document selector))

(defn get-upload-area [] (by-css ".upload-area"))
(defn get-csrf-token [] (.-value (by-css "#__anti-forgery-token")))

(defn listen
  [element event-name handler]
  (.addEventListener element event-name handler false))

(defn prevent-defaults
  [e]
  (.preventDefault e)
  (.stopPropagation e))

(defn on-hover
  [e]
  (prevent-defaults e)
  (gcss/toggle (get-upload-area) "highlight"))

(defn file-from-drag
  "Get the file that was dragged in from a browser event object"
  [e]
  (let [files (.. e -dataTransfer -items)]
    (.getAsFile (first (array-seq files)))))

(defn on-drop
  "Upload the dropped file"
  [e]
  (prevent-defaults e)
  (reset! upload {:file (file-from-drag e) :csrf-token (get-csrf-token)}))

(defn main
  []
  (listen (get-upload-area) "dragover" prevent-defaults)
  (listen (get-upload-area) "drag" prevent-defaults)

  (listen (get-upload-area) "dragenter" on-hover)
  (listen (get-upload-area) "dragleave" on-hover)
  (listen (get-upload-area) "drop" on-drop)
)

(.addEventListener
  js/window
  "DOMContentLoaded"
  (fn [] (main)))
