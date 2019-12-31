package auto.supplierfield

import java.util.function.Supplier

import org.apache.kafka.streams.{StreamsBuilder, Topology}

final class InnerTopology {
  final val someField: Supplier[Topology] = new Supplier[Topology] {
    override final def get(): Topology = {
      val b = new StreamsBuilder
      b.stream("topic-a").to("topic-b")
      b.build
    }
  }
}