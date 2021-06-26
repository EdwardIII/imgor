(defproject imgor "0.1.0-SNAPSHOT"
  :description "Share images on the web"
  :url "https://edwardiii.co.uk"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 ; Compojure - A basic routing library
                 [compojure "1.6.2"]
                 ; Our Http library for client/server
                 [http-kit "2.5.3"]
                 ; Ring defaults - for query params etc
                 [ring/ring-defaults "0.3.3"]
                 ; Clojure data.JSON library
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/clojurescript "1.10.866"]]
  :main ^:skip-aot imgor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}

  :cljsbuild {
    :builds [{
        :source-paths ["src"]
        :compiler {
          :output-to "resources/public/javascripts/main.js"  ; default: target/cljsbuild-main.js
          :optimizations :whitespace
          :pretty-print true}}]}
  :plugins [[lein-cljsbuild "1.1.8"]])

