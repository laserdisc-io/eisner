package auto.fun0field

import org.apache.kafka.streams.{StreamsBuilder, Topology}

final class InnerTopology {
  final val someField: () => Topology = {
    val b = new StreamsBuilder
    b.stream("topic-a").to("topic-b")
    b.build
  }
}