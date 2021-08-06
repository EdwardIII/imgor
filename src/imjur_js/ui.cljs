(ns imjur-js.ui
  (:require [hipo.core :as hipo]
            [imjur-js.file :refer [file-name-from file-size-from]]))

(defn upload-to-dom
  [file-map]
  (hipo/create [:div.uploads
                [:p.name (file-name-from (:file file-map))
                 [:span.size (str " " (get-in file-map [:progress :loaded])) " bytes" ]]]))

(defn upload-request-to-dom
  [upload-request]
  (let [uploads (get upload-request :files)]
    (map (fn [[filename file-map]] (upload-to-dom file-map)) uploads)))

