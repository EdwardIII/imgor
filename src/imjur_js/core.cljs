(ns imjur-js.core
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]
            [goog.dom.classlist :as gcss]
            [goog.dom :as gdom]
            [imjur-js.dom :refer [by-css listen prevent-defaults]]
            [imjur-js.file :refer [file-name-from file-size-from]]
            [imjur-js.ui :refer [upload-request-to-dom]]
            [clojure.spec.alpha :as spec])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def uploads
  "Store uploaded files for use in UI output"
  (atom {:files {}}))

(defn update-uploads!
  "Adds a new file to the existing map of files
  Expects something like:
  {file-name.jpg { :file #object[File [object File]]
                   :progress {,,,}}}
  "
  [file-map]
  (swap! uploads (fn [old-state]
                   (assoc-in old-state [:files (file-name-from (get file-map :file))] file-map))))

(defn upload-file!
  "Upload file to the server.

  Progress output looks like:
  {:file #object[File [object File]], :progress {:direction :upload, :loaded 807009, :total 807009}}

  Done output looks like: {:done {:direction :download, :loaded 120, :total 120}, :file #object[File [object File]]}"
  [file-map csrf-token]
  (go (let [progress (chan)
            response (http/post "/"
                                {:with-credentials? false
                                 :multipart-params [["__anti-forgery-token" csrf-token]
                                                    ["file" (get file-map :file)]]
                                 :progress progress})]
        (update-uploads! (assoc file-map :progress (<! progress)))
        (update-uploads! (assoc file-map :progress (<! progress))))))

(defn upload-area [] (by-css ".upload-area"))
(defn notification-area [] (by-css ".notifications-area"))
(defn notifications-content [] (by-css ".notifications-content"))
(defn clear-notifications [] (by-css ".clear-contents"))
(defn csrf-token [] (.-value (by-css "#__anti-forgery-token")))
()

(defn update-uploads-ui!
  [_ _ _ upload-request]
  (when (not (empty? (get upload-request :files))) (gcss/remove (notification-area) "hide"))
  (gdom/removeChildren (notifications-content))
  (doall (map #(.appendChild (notifications-content) %) (upload-request-to-dom upload-request))))

(add-watch uploads :update-ui update-uploads-ui!)

(defn on-hover
  [e]
  (prevent-defaults e)
  (gcss/toggle (upload-area) "highlight"))

(defn file-from-drag
  "Get the File object that was dragged in from a browser event object"
  [e]
  (let [files (.. e -dataTransfer -items)]
    (.getAsFile (first (array-seq files)))))

(defn on-click-clear
  [e]
  (prevent-defaults e)
  (gdom/removeChildren (notifications-content)))

(defn on-drop
  "Upload the dropped file
  "
  [e]
  (prevent-defaults e)
  (on-hover e)
  (upload-file! {:file (file-from-drag e)} (csrf-token))
  (update-uploads! {:file (file-from-drag e)
                    :progress {}}))

(defn main
  []
  (listen (upload-area) "dragover" prevent-defaults)
  (listen (upload-area) "drag" prevent-defaults)

  (listen (upload-area) "dragenter" on-hover)
  (listen (upload-area) "dragleave" on-hover)

  (listen (upload-area) "drop" on-drop)

  (listen (clear-notifications) "click" on-click-clear))

(listen
  js/window
  "DOMContentLoaded"
  main)
