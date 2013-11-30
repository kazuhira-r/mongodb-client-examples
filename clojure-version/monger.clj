(use '[leiningen.exec :only (deps)])
(deps '[[com.novemberain/monger "1.5.0"]])

(ns monger.getting-started
  (:require [monger.core :as mc]
            [monger.collection :as mcol]
            [monger.operators :as mop]))

;;(mg/connect!)  ;; ローカルホストに、デフォルトポートで接続

(mc/connect! {:host "localhost" :port 27017}) ;; ホスト名とポートを明示

;; 使用するデータベース
(mc/set-db! (mc/get-db "tutorial"))

;; コレクションの名前は、各関数の引数に指定する

;; データの登録
(mcol/insert "nosql" {:name "MongoDB" :type "Document Database"})
(mcol/insert "nosql" {:name "CouchDB" :type "Document Database"})
(mcol/insert "nosql" {:name "Memcached" :type "Key Value Store"})

;; 複数件
(mcol/insert-batch "nosql" [{:name "Apache Cassandra" :type "Column Database"}
                            {:name "Apache HBase" :type "Column Database"}])

;; 検索
(dorun
 (for [object (mcol/find "nosql")]
   (printf "find => %s%n" object)))
(dorun
 (for [object (mcol/find "nosql" {:type "Column Database"})]
   (printf "find(condition) => %s%n" object)))

;; Clojureのマップとしても取得できます
(dorun
 (for [m (mcol/find-maps "nosql")]
   (printf "find-maps :name=%s :type=%s%n" (:name m) (:type m))))
(dorun
 (for [m (mcol/find-maps "nosql" {:type "Column Database"})]
   (printf "find-maps(condition) :name=%s :type=%s%n"
           (:name m)
           (:type m))))

;; find-one
(printf "find-one => %s%n" (mcol/find-one "nosql" {}))
(printf "find-one(condition) => %s%n"
        (mcol/find-one "nosql" {:name "Apache Cassandra"}))

;; find-oneも、マップとして戻せます
(printf "find-one-map => %s%n" (mcol/find-one-as-map "nosql" {}))
(printf "find-one-map(condition) => %s%n"
        (mcol/find-one-as-map "nosql" {:name "Apache Cassandra"}))

;; 件数
(printf "count => %d%n" (mcol/count "nosql"))
(printf "count(condition) => %s%n"
        (mcol/count "nosql" {:name "Apache Cassandra" :type "Column Database"}))

;; 更新
;; $set
(mcol/update "nosql" {:name "MongoDB"} {mop/$set {:url "http://www.mongodb.org/"}})
(mcol/update "nosql" {:name "Memcached"} {mop/$unset {:type 1}})

;; save
(mcol/save "nosql" {:name "Redis" :type "Key Value Store"})
(let [redis (mcol/find-one-as-map "nosql" {:name "Redis"})
      updated (assoc redis :url "http://redis.io/")]
  (mcol/save "nosql" updated))

;; ドキュメントの削除
(mcol/remove "nosql" {:name "Apache HBase"})

(dorun
 (for [obj (mcol/find "nosql")]
   (printf "find => %s%n" obj)))

;; コレクションの全データ削除
(mcol/remove "nosql")

(mc/disconnect!)