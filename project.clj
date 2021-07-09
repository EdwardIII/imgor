(defproject imjur "0.1.0-SNAPSHOT"
  :description "Share images on the web"
  :url "https://edwardiii.co.uk"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [compojure "1.6.2"]
                 [http-kit "2.5.3"]
                 [ring/ring-defaults "0.3.3"]
                 [org.clojure/data.json "2.3.1"]
                 [org.clojure/clojurescript "1.10.866"]
                 [cljs-http "0.1.46"]
                 [hipo "0.5.2"]

                 ; TODO: split into dev deps
                 [ring/ring-core "1.9.3"]
                 [ring/ring-jetty-adapter "1.9.3"]]

  :main ^:skip-aot imjur.core
  :profiles {:dev 
             {:dependencies [[com.bhauman/figwheel-main "0.2.13"]
                             [com.bhauman/rebel-readline-cljs "0.1.4"]
                                  ;[binaryage/devtools "0.9.0"]
                              ]
                   }

             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             }

  :plugins [[lein-cljsbuild "1.1.8"]]

  :cljsbuild {
              :builds [ { :id "min" 
                         :source-paths ["src"]
                         :compiler {
                                    :main "imjur-js.core"
                                    :target "resources/public/js"
                                    :asset-path "js/out"
                                    :output-to "resources/public/js/main.min.js"
                                    :output-dir "resources/public/js/out"
                                    :optimizations :advanced}}]}

  :figwheel {:ring-handler imjur.core/app
             :css-dirs ["resources/public/css"]
             :repl true}

  :aliases {"fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "imjur" "-r"]})
