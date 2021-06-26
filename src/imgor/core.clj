(ns imgor.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:gen-class))

(def server-config
  {:params {:urlencoded true,
     :multipart true,
     :nested true,
     :keywordize true},
     :cookies false,
     :session {:flash true, :cookie-attrs {:http-only true, :same-site :strict}},
      ; TODO: enable anti-forgery and xss-protection
     :security {:anti-forgery true, :xss-protection {:enable? true, :mode :block}, :frame-options :sameorigin, :content-type-options :nosniff},
     :static {:resources "public"},
     :responses {:not-modified-responses true, :absolute-redirects true, :content-types true, :default-charset "utf-8"}})

(defn index-template
  []
  (format (slurp "resources/index.html") (anti-forgery-field)))

(defn index-handler
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body    (index-template)})

(defn temp-file-path
  [req]
  (:path (bean (get-in req [:params :file :tempfile]))))

(defn upload-destination-path
  [req]
  (str "resources/public/uploads/" (get-in req [:params :file :filename])))

(defn public-uploads-path
  [req]
  (str "/uploads/" (get-in req [:params :file :filename])))

(defn upload-handler [req]
  (let [custom-path (upload-destination-path req)]
  (do
    (io/copy (io/file (temp-file-path req)) (io/file custom-path))
    {:status 200
     :headers  {"Content-Type" "text/html"}
     :body (str "File now available for download at: http://localhost:3000" (public-uploads-path req))})))

(defroutes app-routes
  (GET "/" [] index-handler)
  (POST "/" [] upload-handler)
  (route/not-found "Error, page not found!"))

(defn -main
  "Upload images to share on the web"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (do
      (server/run-server (wrap-defaults #'app-routes server-config) {:port port})
      (println "Server started on port" port)
      )))
