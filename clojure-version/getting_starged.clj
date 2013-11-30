(use '[leiningen.exec :only (deps)])
(deps '[[org.mongodb/mongo-java-driver "2.11.2"]])

(ns mongodb.getting-started
  (:import (com.mongodb MongoClient WriteConcern BasicDBObject)))

;; MongoDBに
(with-open [client (MongoClient. "localhost" 27017)]
  (let
      ;; 使用するデータベース
      [db (.getDB client "tutorial")
       ;; コレクション名を「nosql」にします
       nosql (.getCollection db "nosql")]

    ;; データの登録
    ;; 1件ずつですが、このAPIだとメソッドが推測できないっぽい？
    (doto nosql
      (.insert (into-array [(-> (BasicDBObject. "name" "MongoDB")
                                (.append "type" "Document Database"))]))
      (.insert (into-array [(-> (BasicDBObject. "name" "CouchDB")
                                (.append "type" "Document Database"))]))
      (.insert (into-array [(-> (BasicDBObject. "name" "Memcached")
                                (.append "type" "Key Value Store"))])))

    ;; 複数件
    (let [documents (list (-> (BasicDBObject. "name" "Apache Cassandra")
                              (.append "type" "Column Database"))
                          (-> (BasicDBObject. "name" "Apache HBase")
                              (.append "type" "Column Database")))]
      (.insert nosql documents))

    ;; 検索
    (dorun
     (for [object (.find nosql)]
       (printf "find => %s%n" object)))
    (dorun
     (for [object (.find nosql (BasicDBObject. "type" "Column Database"))]
       (printf "find(conditioin) => %s%n" object)))

    ;; findOne
    (printf "findOne => %s%n" (.findOne nosql))
    (printf "findOne(condition) => %s%n"
            (.findOne nosql (BasicDBObject. "name" "Apache Cassandra")))

    ;; 件数
    (printf "count => %d%n" (.count nosql))
    (printf "count(condition) => %d%n"
            (.count nosql (-> (BasicDBObject. "name" "Apache Cassandra")
                              (.append "type" "Column Database"))))

    ;; 更新
    ;; $set
    (.update nosql
             (BasicDBObject. "name" "MongoDB")
             (BasicDBObject. "$set" (BasicDBObject. "url" "http://www.mongodb.org/")))
    (.update nosql
             (BasicDBObject. "name" "Memcached")
             (BasicDBObject. "$unset" (BasicDBObject. "type" 1)))

    ;; save
    (.save nosql (-> (BasicDBObject. "name" "Redis")
                     (.append "type" "Key Value Store")))
    (let [redis (.findOne nosql (BasicDBObject. "name" "Redis"))]
      (.append redis "url" "http://redis.io/")
      (.save nosql redis))

    ;; ドキュメントの削除
    (.remove nosql (BasicDBObject. "name" "Apache HBase"))

    (dorun
     (for [object (.find nosql)]
       (printf "find => %s%n" object)))

    ;; コレクションの全データ削除
    (.remove nosql (BasicDBObject.))

    ))
