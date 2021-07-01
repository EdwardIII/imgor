(defproject imjur "0.1.0-SNAPSHOT"
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
                 [org.clojure/clojurescript "1.10.866"]
                 [cljs-http "0.1.46"]
                 [hipo "0.5.2"]

                 ; TODO: split into dev deps
                 [ring/ring-devel "1.8.0"]]

  :main ^:skip-aot imjur.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}

  :cljsbuild {
    :builds [{
        :source-paths ["src"]
        :compiler {
          :output-to "resources/public/javascripts/main.js"
          :output-dir "resources/public/javascripts/"
          :optimizations :whitespace
          :pretty-print true
          :source-map "resources/public/javascripts/main.js.map"}}]}
  :plugins [[lein-cljsbuild "1.1.8"]])
