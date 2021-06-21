import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object kuozhan1 {
  val target = 'b'

  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //Windows:nc -l -p 9999
    val text = env.socketTextStream("localhost", 9999)

    val stream = text.flatMap { _.toLowerCase.split("")
      .filter { _.contains(target) } }
      .map { (_, 1) }
      .keyBy(0)
      .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
      .sum(1)

    stream.print()

    env.execute("Window Stream WordCount")
  }
}