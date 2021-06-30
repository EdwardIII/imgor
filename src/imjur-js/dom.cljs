(ns imjur-js.dom)
"Not sure if we want to rely on google closure for all dom functions
so just creating a few simple utilities here"

(defn by-css
  [selector]
  "Get single dom element by selector"
  (.querySelector js/document selector))

(defn listen
  [element event-name handler]
  (.addEventListener element event-name handler false))
