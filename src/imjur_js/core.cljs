(ns imjur-js.core
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]
            [goog.dom.classlist :as gcss]
            [hipo.core :as hipo]
            [imjur-js.dom :refer [by-css listen]]
            [clojure.spec.alpha :as spec])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;lastModified: 1625089304422
;lastModifiedDate: Wed Jun 30 2021 22:41:44 GMT+0100 (British Summer Time) {}
;name: "rodrigo-soares-hrO9ZbmGD00-unsplash.jpg"
;size: 1100534
;type: "image/jpeg"
;webkitRelativePath: ""
(spec/def ::lastModified       number?)
(spec/def ::lastModifiedDate   any?)
(spec/def ::name               string?)
(spec/def ::size               number?)
(spec/def ::type               string?)
(spec/def ::webkitRelativePath string?)

(spec/def ::file
  (spec/keys :req-un [
                   ::name
                   ::size
                   ::type
                   ]
             :opt-un [::lastModified
                   ::lastModifiedDate
                   ::webkitRelativePath
                   ]))

; TODO: Keep a collection of files
{:file {:name :size :type :status}}
[:in-progress
:uploaded
:failed]

(def upload (atom {:file nil
                   :csrf-token nil}))

; TODO: show these nicely in the UI
(defn uploader
  [_ _ _ upload-request]
  (go (let [progress (chan)
            response (http/post "/"
                                {:with-credentials? false
                                 :multipart-params [["__anti-forgery-token" (upload-request :csrf-token)]
                                                    ["file" (upload-request :file)]]
                                 :progress progress})]
        (println (<! progress))
        (println (<! response)))))

(defn file-name-from
  [upload-request]
  (.-name (:file upload-request)))

(defn file-size-from
  [upload-request]
  (.-size (:file upload-request)))

(defn update-uploads-ui
  [_ _ _ upload-request]
  (let [el (hipo/create [:div.uploads
                         [:p.name (file-name-from upload-request)
                          [:span.size (str " " (file-size-from upload-request) " bytes" )]]
                         ])]
    (.appendChild js/document.body el)))

(add-watch upload :upload-file uploader)
(add-watch upload :update-ui update-uploads-ui)

(defn get-upload-area [] (by-css ".upload-area"))
(defn get-csrf-token [] (.-value (by-css "#__anti-forgery-token")))

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
  (on-hover e)
  (reset! upload {:file (file-from-drag e) :csrf-token (get-csrf-token)}))

(defn main
  []
  (listen (get-upload-area) "dragover" prevent-defaults)
  (listen (get-upload-area) "drag" prevent-defaults)

  (listen (get-upload-area) "dragenter" on-hover)
  (listen (get-upload-area) "dragleave" on-hover)

  (listen (get-upload-area) "drop" on-drop))

(listen
  js/window
  "DOMContentLoaded"
  main)
