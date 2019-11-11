import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.{StreamPartitioner, WallclockTimestampExtractor}

final class SimpleTopology extends Topology {
  addSource(
    Topology.AutoOffsetReset.EARLIEST,
    "sensor-a",
    new WallclockTimestampExtractor(),
    Serdes.String().deserializer(),
    Serdes.String().deserializer(),
    "topic-a"
  )
  addSource(
    Topology.AutoOffsetReset.EARLIEST,
    "sensor-b",
    new WallclockTimestampExtractor(),
    Serdes.String().deserializer(),
    Serdes.String().deserializer(),
    "topic-b"
  )
  addSink(
    "to-the-world",
    "output-topic",
    Serdes.String().serializer(),
    Serdes.String().serializer(),
    "sensor-a", "sensor-b"
  )
}