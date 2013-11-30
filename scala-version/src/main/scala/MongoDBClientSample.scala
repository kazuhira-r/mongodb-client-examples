import com.mongodb.casbah.Imports._

object MongoDBClientSample {
  def main(args: Array[String]): Unit = {
    // MongoDBに接続
    def mongo = MongoClient("localhost", 27017)
    // ローカルで動かすなら、以下でもOK
    // def mongo = MongoClient()

    try {
      // 使用するデータベース
      val db = mongo("tutorial")

      // コレクション名を「nosql」にします
      val nosql = db("nosql")

      // データの登録
      // 1件ずつ
      nosql.insert(MongoDBObject("name" -> "MongoDB",
                                 "type" -> "Document Database"))
      // += メソッドでもOK
      nosql += MongoDBObject("name" -> "CouchDB",
                             "type" -> "Document Database")
      // 実は、Mapでもinsertできます…
      /*
      nosql += Map("name" -> "CouchDB",
                   "type" -> "Document Database")
      */
      // Builderを使用
      val memcachedBuilder = MongoDBObject.newBuilder
      memcachedBuilder += "name" -> "Memcached"
      memcachedBuilder += "type" -> "Key Value Store"
      nosql += memcachedBuilder.result

      // 複数件
      nosql.insert(MongoDBObject("name" -> "Apache Cassandra",
                                 "type" -> "Column Database"),
                   MongoDBObject("name" -> "Apache HBase",
                                 "type" -> "Column Database"))

      // 検索
      // find
      nosql.find.foreach(doc => println(s"find => $doc"))
      nosql.find(MongoDBObject("type" -> "Column Database"))
        .foreach(doc => println(s"find(condition) => $doc"))

      // findOne
      // 戻り値はOption
      nosql.findOne.foreach(doc => println(s"findOne => $doc"))
      nosql.findOne(MongoDBObject("name" -> "Apache Cassandra"))
        .foreach(doc => println(s"findOne(condition) => $doc"))

      // 件数
      println("count => " + nosql.count((dbObj: DBObject) => true))
      println("count(condition) => " +
              nosql.count((dbObj: DBObject) => {
                dbObj("name") == "Apache Cassandra" &&
                dbObj("type") == "Column Database"
              }))
      // find.countした方がいい？
      // println("count => " + nosql.find.count)
      // println("count(condition) => " +
      //         nosql.find(MongoDBObject("name" -> "Apache Cassandra",
      //                                  "type" -> "Column Database")).count)

      // 更新
      // $set
      nosql.update(MongoDBObject("name" -> "MongoDB"), $set ("url" -> "http://www.mongodb.org/"))
      // $unset
      nosql.update(MongoDBObject("name" -> "Memcached"), $unset ("type"))
      
      // save
      nosql.save(MongoDBObject("name" -> "Redis", "type" -> "Key Value Store"))
      nosql.findOne(MongoDBObject("name" -> "Redis"))
        .foreach(doc => nosql.save(doc += ("url" -> "http://redis.io/")))

      // ドキュメントの削除
      nosql -= MongoDBObject("name" -> "Apache HBase")
      // 以下でもOK
      // nosql.remove(MongoDBObject("name" -> "Apache HBase"))

      nosql.find.foreach(println)

      // コレクション内の全データを削除
      nosql -= MongoDBObject.empty
      // 以下でもOK
      // nosql.remove(MongoDBObject.empty)
    } finally {
      mongo.close()
    }
  }
}
