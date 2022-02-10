(ns imjur-js.ui-test
  (:require
    [cljs.test :refer-macros [deftest is testing]]
    [clojure.string :as string]
    [imjur-js.ui :refer [upload-to-dom upload-request-to-dom]]))

(def file-map1 
  {:file (js/File. ["binary data of beavis"] "beavis.jpg" {:type "image/jpeg"})
   :progress {:loaded 1337}})

(def file-map2 
  {:file (js/File. ["binary data of butthead"] "butthead" {:type "image/jpeg"})
   :progress {:loaded 4321}})

(defn stringify [element] (. element -innerHTML))

(deftest upload-to-dom-filename-test
  (is (string/includes? (stringify (upload-to-dom file-map)) "beavis.jpg")))

(deftest upload-to-dom-file-bytes-test
  (is (string/includes? (stringify (upload-to-dom file-map)) "1337 bytes")))
