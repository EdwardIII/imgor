(ns imjur.core-test
  (:gen-class)
  (:require [clojure.test :as t]
            [clojure.string :refer [includes?]]
            [clojure.data.json :as json]
            [imjur.core :refer :all]))

(defmethod t/assert-expr 'assert-contains? [msg form]
  "Assert whether expected string exists in actual string
Example: (assert-contains \"expected\" \"actual\")"
  `(let [expected# ~(nth form 1)
         actual# ~(nth form 2)
         result# (includes? actual# expected#)]
     (do (t/do-report
         {:type (if result# :pass :fail)
          :message ~msg
          :expected (format "Did not find \"%s\" in \"%s\"" expected# actual#) 
          :actual actual#}
         )
        result#)))


(def tempfile {:path "/tmp/h4nk.jpg"})

(def sample-request {:params
                     {:file
                      {:filename "hank.jpg"
                       :tempfile (java.io.File. "/tmp/r4nd0m-n4m3.jpg")}}})

(t/deftest the-utilities
  (t/testing "public-uploads-path Gives a relative public upload url for a file request."
    (t/is (= "/uploads/hank.jpg" (public-uploads-path sample-request))))

  (t/testing "temp-file-path gives the on-disk path to a file"
    (t/is (= "/tmp/r4nd0m-n4m3.jpg" (temp-file-path sample-request)))))

(t/deftest the-responses
  (t/testing "success-response gives url to download file"
    (t/is (assert-contains? "http://localhost:3000/uploads/hank.jpg"
                         (-> sample-request
                             success-response
                             :body
                             json/read-str
                             (get "status"))))))

(t/deftest always-fail (t/is (= true false))
  )
