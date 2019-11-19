package snippet

import org.apache.kafka.streams.{StreamsBuilder, Topology}

final case class Settings(source: String, destination: String)

final object EisnerTopology {
  final val myTopology: Settings => Topology = settings => {
    val b = new StreamsBuilder
    b.stream(settings.source).to(settings.destination)
    b.build
  }
}