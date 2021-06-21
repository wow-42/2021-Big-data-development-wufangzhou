import java.util.{Properties, UUID}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import java.sql.{Connection, DriverManager, PreparedStatement}

object kuozhan4 {
  //输入的kafka主题名称
  val inputTopic = "wfz_kuozhan3_4"
  //kafka地址
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val temp = new MysqlWriter
    temp.upload

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    inputKafkaStream.writeUsingOutputFormat()

    env.execute()

  }

}

case class JDBCSink()

class MysqlWriter {

  val url = "jdbc:mysql://localhost:3306/mysql?serverTimezone=GMT"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "123456"

  var insertSql: PreparedStatement=_

  def upload : Unit = {

    val  connection = getConnection()
    try {
      val statement = connection.createStatement()
      insertSql = connection.prepareStatement("insert into test values(\"a\",20)")

    }
    finally {
      connection.close()
    }
  }

  def getConnection(): Connection = {
    DriverManager.getConnection(url, username, password)
  }

  def close(conn: Connection): Unit = {
    try {
      if (!conn.isClosed() || conn != null) {
        conn.close()
      }
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    }
  }

}

case class SensorReading(str: String, toLong: Long, toDouble: Double)