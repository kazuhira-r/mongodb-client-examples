@Grab('com.gmongo:gmongo:1.0')
import com.gmongo.GMongo

// MongoDBに接続
def mongo = new GMongo('localhost', 27017)
// ローカルで動かすなら、以下でもOK
// def mongo = new GMongo()

// 使用するデータベース
def db = mongo.getDB('tutorial')

// コレクション名を「nosql」にします

// データの登録
// 1件ずつ
db.nosql.insert([name: 'MongoDB', type: 'Document Database'])
db.nosql.insert(name: 'CouchDB', type: 'Document Database')
db.nosql << [name: 'Memcached', type: 'Key Value Store']
// 複数件
db.nosql << [
    [name: 'Apache Cassandra', type: 'Column Database'],
    [name: 'Apache HBase', type: 'Column Database']
]

// 検索
// find
db.nosql.find().each { println("find => $it") }
db.nosql.find(type: 'Column Database').each { println("find(condition) => $it") }

// findOne
println('findOne => ' +
            db.nosql.findOne())
println('findOne(condition) => ' +
            db.nosql.findOne(name: 'Apache Cassandra'))

// 件数
println('count => ' +
            db.nosql.count())
println('count(condition) => ' +
            db.nosql.count(name: 'Apache Cassandra', type: 'Column Database'))

// 更新
// $set
db.nosql.update([name: 'MongoDB'], [$set: [url: 'http://www.mongodb.org/']])
// $unset
db.nosql.update([name: 'Memcached'], [$unset: [type: 1]])

// save
db.nosql.save(name: 'Redis', type: 'Key Value Store')
db.nosql.findOne(name: 'Redis').with {
    db.nosql.save(it << [url: 'http://redis.io/'])
}

// ドキュメントの削除
db.nosql.remove(name: 'Apache HBase')

db.nosql.find().each { println(it) }

// コレクションの全データを削除
db.nosql.remove([:])

// 接続終了
mongo.close()
