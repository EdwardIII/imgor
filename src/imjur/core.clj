(ns imjur.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.reload :refer [wrap-reload]]
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
     :security {:anti-forgery true, :xss-protection {:enable? true, :mode :block}, :frame-options :sameorigin, :content-type-options :nosniff},
     :static {:resources "public"},
     :responses {:not-modified-responses true, :absolute-redirects true, :content-types true, :default-charset "utf-8"}})

(defn index-template
  "Render index template"
  []
  (format (slurp "resources/index.html") (anti-forgery-field)))

(defn index-handler
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body    (index-template)})

(defn temp-file-path
  "Get the temporary path of an uploaded file on disk"
  [req]
  (:path (bean (get-in req [:params :file :tempfile]))))

(defn upload-destination-path
  "The path on disk where uploads get saved to"
  [req]
  (str "resources/public/uploads/" (get-in req [:params :file :filename])))

(defn public-uploads-path
  "The directory where uploads will be accessible over the web"
  [req]
  (str "/uploads/" (get-in req [:params :file :filename])))

(defn upload-handler [req]
  "Save the uploaded file to disk"
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

(def reloadable-routes
  "Reload application if files are changed"
  (wrap-reload #'app-routes))

(def choose-routes
  "Choose between auto-reloading when code changes, or not. default: Production, no auto-reloading"
  (let [env (or (System/getenv "IMJUR_ENV") "production")]
    (if (= env "production") #'app-routes #'reloadable-routes)))

(defn -main
  "Upload images to share on the web"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (do
      (server/run-server (wrap-defaults choose-routes server-config) {:port port})
      (println "Server started on port" port)
      )))
