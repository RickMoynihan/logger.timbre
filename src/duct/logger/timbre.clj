(ns duct.logger.timbre
  (:require [duct.core.protocols :as p]
            [integrant.core :as ig]
            [taoensso.timbre :as timbre]))

(defmethod ig/init-key ::println [_ options]
  (timbre/println-appender options))

(defmethod ig/init-key ::spit [_ options]
  (timbre/spit-appender options))

(derive :duct.logger/timbre :duct/logger)

(defrecord TimbreLogger [config]
  p/Logger
  (-log [_ level ns-str file line event data]
    (cond
      (instance? Throwable data)
      (timbre/log! level :p (event)
                   {:config config, :?ns-str ns-str, :?file file, :?line line, :?err data})
      (nil? data)
      (timbre/log! level :p (event)
                   {:config config, :?ns-str ns-str, :?file file, :?line line})
      :else
      (timbre/log! level :p (event data)
                   {:config config, :?ns-str ns-str, :?file file, :?line line}))))

(defmethod ig/init-key :duct.logger/timbre [_ config]
  (->TimbreLogger config))