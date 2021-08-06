(ns imjur-js.file)

(defn file-name-from
  [file]
  (.-name file))

(defn file-size-from
  [file]
  (or (.-size file) 0))
