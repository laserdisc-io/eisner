# Eisner
[![Build Status](https://travis-ci.org/laserdisc-io/eisner.svg?branch=master)](https://travis-ci.org/laserdisc-io/eisner)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/sbt-eisner/badge.svg?kill_cache=1&color=orange)](https://search.maven.org/artifact/io.laserdisc/sbt-eisner/)

Eisner is a Kafka (Streams' topologies) translator

## Motivating example

Kafka Stream's topologies can quickly become complex to grok without picturing them visually.

Eisner fills this gap by offering to developers an sbt plugin to create a graphical representation of Kafka Stream's Topology(ies).
It took ideas and inspiration from [this](https://zz85.github.io/kafka-streams-viz/) online tool by Joshua Koo.

For example, assuming the following topology description:

```
Topology
Sub-topologies:
Sub-topology: 0
	Source:  KSTREAM-SOURCE-0000000000 (topics: [conversation-meta])
	--> KSTREAM-TRANSFORM-0000000001
	Processor: KSTREAM-TRANSFORM-0000000001 (stores: [conversation-meta-state])
	--> KSTREAM-KEY-SELECT-0000000002
	<-- KSTREAM-SOURCE-0000000000
	Processor: KSTREAM-KEY-SELECT-0000000002 (stores: [])
	--> KSTREAM-FILTER-0000000005
	<-- KSTREAM-TRANSFORM-0000000001
	Processor: KSTREAM-FILTER-0000000005 (stores: [])
	--> KSTREAM-SINK-0000000004
	<-- KSTREAM-KEY-SELECT-0000000002
	Sink: KSTREAM-SINK-0000000004 (topic: count-resolved-repartition)
	<-- KSTREAM-FILTER-0000000005
Sub-topology: 1
	Source: KSTREAM-SOURCE-0000000006 (topics: [count-resolved-repartition])
	--> KSTREAM-AGGREGATE-0000000003
	Processor: KSTREAM-AGGREGATE-0000000003 (stores: [count-resolved])
	--> KTABLE-TOSTREAM-0000000007
	<-- KSTREAM-SOURCE-0000000006
	Processor: KTABLE-TOSTREAM-0000000007 (stores: [])
	--> KSTREAM-SINK-0000000008
	<-- KSTREAM-AGGREGATE-0000000003
	Sink: KSTREAM-SINK-0000000008 (topic: streams-count-resolved)
	<-- KTABLE-TOSTREAM-0000000007
```

Eisner will first translate it into the following [`DOT`](https://en.wikipedia.org/wiki/DOT_(graph_description_language)) representation:

```
digraph G {
	label = "Kafka Streams Topology"
	subgraph cluster_0 {
		label = " Sub-\ntopology: 0";
		style = filled;
		color = lightgrey;
		node [style = filled, color = white];
		"KSTREAM-\nSOURCE-\n0000000000" -> "KSTREAM-\nTRANSFORM-\n0000000001";
		"KSTREAM-\nTRANSFORM-\n0000000001" -> "KSTREAM-\nKEY-\nSELECT-\n0000000002";
		"KSTREAM-\nKEY-\nSELECT-\n0000000002" -> "KSTREAM-\nFILTER-\n0000000005";
		"KSTREAM-\nFILTER-\n0000000005" -> "KSTREAM-\nSINK-\n0000000004";
	}
	subgraph cluster_1 {
		label = " Sub-\ntopology: 1";
		style = filled;
		color = lightgrey;
		node [style = filled, color = white];
		"KSTREAM-\nSOURCE-\n0000000006" -> "KSTREAM-\nAGGREGATE-\n0000000003";
		"KSTREAM-\nAGGREGATE-\n0000000003" -> "KTABLE-\nTOSTREAM-\n0000000007";
		"KTABLE-\nTOSTREAM-\n0000000007" -> "KSTREAM-\nSINK-\n0000000008";
	}
	"conversation-\nmeta" -> "KSTREAM-\nSOURCE-\n0000000000";
	"KSTREAM-\nTRANSFORM-\n0000000001" -> "conversation-\nmeta-\nstate";
	"KSTREAM-\nSINK-\n0000000004" -> "count-\nresolved-\nrepartition";
	"count-\nresolved-\nrepartition" -> "KSTREAM-\nSOURCE-\n0000000006";
	"KSTREAM-\nAGGREGATE-\n0000000003" -> "count-\nresolved";
	"KSTREAM-\nSINK-\n0000000008" -> "streams-\ncount-\nresolved";
	"conversation-\nmeta" [shape = rect];
	"count-\nresolved-\nrepartition" [shape = rect];
	"streams-\ncount-\nresolved" [shape = rect];
	"conversation-\nmeta-\nstate" [shape = cylinder];
	"count-\nresolved" [shape = cylinder];
}
```

then, leveraging the JS wrapper of `graphviz` (see [here](https://github.com/mdaines/viz.js)), Eisner will convert it into the following [`SVG`](https://en.wikipedia.org/wiki/Scalable_Vector_Graphics):

<img src="https://raw.githubusercontent.com/laserdisc-io/eisner/master/src/test/resources/topology1.svg?sanitize=true" width="141" height="699"/>

and ultimately, it will add some "hand drawn" feeling to it using [roughjs](https://roughjs.com/), to turn it into a new `SVG` like the following:

<img src="https://raw.githubusercontent.com/laserdisc-io/eisner/master/src/test/resources/topology1-rough.svg?sanitize=true" width="141" height="699"/>

## Usage

Import the plugin by placing the following line in `project/plugins.sbt`:

```
// check the current version on Maven Central (or use the badge above)
addSbtPlugin("io.laserdisc" %% "sbt-eisner" % version)
```

Then, in your `build.sbt`, enable Eisner by adding the following to your module/project where your Kafka Stream's Topology(ies) definitions are:

```
enablePlugins(EisnerPlugin)
```

Finally, trigger Eisner's Topology to SVG translation by running within sbt:

```
sbt> eisner
```

That's it (_potentially_)! Eisner is capable of scanning your classes searching for:
- zero-arg constructor classes that extend `org.apache.kafka.streams.Topology`
- fields or zero-arg methods that return `org.apache.kafka.streams.Topology`
- fields or zero-arg methods that return `scala.Function0[org.apache.kafka.streams.Topology]`
- fields or zero-arg methods that return `java.util.function.Supplier[org.apache.kafka.streams.Topology]`

SVGs will be created for each, named after the class(+field/method) that defines the Topology.
Currently, files are generated in `target/streams/_global/eisner/_global/streams/`.

## Tweaking with it

Currently, Eisner supports a handful of options, namely:

- `eisnerTopologies`: a `Seq[String]` representing the fully qualified names of classes implementing `org.apache.kafka.streams.Topology`. This is useful in all cases where you need to control which SVGs get generated
- `eisnerTopologiesSnippet`: a `Option[String]` representing a Scala snippet (inclusive of all necessary imports) that evaluates to a `Seq[(String, org.apache.kafka.streams.Topology)]`, where the `String` represents the `package.name` you want to give to the target file (dots will be converted to path separators). This is useful in all cases where automatic classloader scanning would not work, e.g. because you define your topologies in non zero-args methods or in fields that return `scala.Function1`. See [here](https://raw.githubusercontent.com/laserdisc-io/eisner/master/src/sbt-test/sbt-eisner/snippet/src/main/scala/snippet/EisnerTopology.scala) for a practical example.

## License

Eisner is licensed under the **[MIT License](LICENSE)** (the "License"); you may not use this software except in
compliance with the License.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
