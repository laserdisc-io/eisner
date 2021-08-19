package auto.method

import org.apache.kafka.streams.{StreamsBuilder, Topology}

final class InnerTopology {
  final def someMethod: Topology = {
    val b = new StreamsBuilder
    b.stream("topic-a").to("topic-b")
    b.build
  }
}
