;; This test runner is intended to be run from the command line
(ns imjur-js.test-runner
  (:require
    ;; require all the namespaces that you want to test
    [imjur-js.core-test]
    [imjur-js.file-test]
    [imjur-js.ui-test]
    [figwheel.main.testing :refer [run-tests-async]]))

(defn -main [& args]
  (run-tests-async 5000))
