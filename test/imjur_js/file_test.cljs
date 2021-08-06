(ns imjur-js.file-test
  (:require
    [cljs.test :refer-macros [deftest is testing]]
    [imjur-js.file :refer [file-name-from file-size-from]]))

(def file (js/File. ["binary data of beavis"] "beavis.jpg" {:type "image/jpeg"}))
(def empty-file (js/File. [] "empty.jpg" {:type "image/jpeg"}))

(deftest file-name-from-test
  (is (= "beavis.jpg" (file-name-from file))))

(deftest file-size-from-test
  (is (= 21 (file-size-from file))))

(deftest empty-file-size-from-test
  (is (= 0 (file-size-from empty-file))))
