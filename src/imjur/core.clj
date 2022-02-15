(ns imjur.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as server]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [clojure.spec.alpha :as spec]
            [clojure.data.json :as json]))

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

(spec/def ::filename      string?)
(spec/def ::content-type  string?)
(spec/def ::size          (spec/and number? pos?))
(spec/def ::tempfile      (fn [file](instance? java.io.File file)))

; Example:
; {:filename "trump.gif"
; :content-type "image/gif"
; :tempfile #object[java.io.File 0x3b9855c "/var/folders/6d/wnw21pgn2y90s8y1r7slm1800000gn/T/ring-multipart-2231893712933848300.tmp"]
; :size 2516456}
(spec/def ::file
  (spec/keys :req-un [
                   ::filename
                   ::content-type
                   ::size
                   ]
             :opt-un [::tempfile]))

(defn success-response
  [req]
    {:status 200
     :headers  {"Content-Type" "application/json"}
     :body (json/write-str {:status (str "File now available for download at: http://localhost:3000" (public-uploads-path req))})})
                                        ;; TODO: Get url dynamically


(defn failure-response
  [req]
    {:status 422
     :headers  {"Content-Type" "application/json"}
     :body (json/write-str {:status "Upload not valid"
                            :reasons (spec/explain-str ::file (get-in req [:params :file]))})}

  (defn upload-handler [req]
    "Save the uploaded file to disk"
    (let [custom-path (upload-destination-path req)]
      (if (spec/valid? ::file (get-in req [:params :file]))
        (do
          (io/copy (io/file (temp-file-path req)) (io/file custom-path))
          (success-response req))
        (failure-response req))))

  (defroutes app-routes
    (GET "/" [] index-handler)
    (POST "/" [] upload-handler)
    (route/not-found "Error, page not found!")))

(def reloadable-routes
  "Reload application if files are changed"
  (wrap-reload #'app-routes))

(def choose-routes
  "Choose between auto-reloading when code changes, or not. default: Production, no auto-reloading"
  (let [env (or (System/getenv "IMJUR_ENV") "production")]
    (if (= env "production") #'app-routes #'reloadable-routes)))

(def app (wrap-defaults choose-routes server-config))

(defn -main
  "Upload images to share on the web"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (do
      (server/run-server (wrap-defaults choose-routes server-config) {:port port})
      (println "Server started on port" port))))

