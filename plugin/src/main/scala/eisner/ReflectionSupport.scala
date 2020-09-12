package eisner

import java.util.function.Supplier

import org.apache.kafka.streams.Topology
import org.clapper.classutil.ClassInfo
import sbt.util.Logger

private[eisner] trait ReflectionSupport {
  import Accessibility._

  private[this] final val dotRegex                             = "\\.".r
  private[this] final val function0ClassName                   = classOf[Function0[_]].getName
  private[this] final val supplierClassName                    = classOf[Supplier[_]].getName
  private[this] final val kafkaTopologyClass                   = classOf[Topology]
  private[this] final val kafkaTopologyClassName               = kafkaTopologyClass.getName
  private[this] final val kafkaTopologyFieldDescriptor         = s"L${dotsToSlashes(kafkaTopologyClassName)};"
  private[this] final val kafkaTopologyMethodDescriptor        = s"()$kafkaTopologyFieldDescriptor"
  private[this] final val kafkaTopologyFun0FieldSignature      = s"L${dotsToSlashes(function0ClassName)}<$kafkaTopologyFieldDescriptor>;"
  private[this] final val kafkaTopologyFun0MethodSignature     = s"()$kafkaTopologyFun0FieldSignature"
  private[this] final val kafkaTopologySupplierFieldSignature  = s"L${dotsToSlashes(supplierClassName)}<$kafkaTopologyFieldDescriptor>;"
  private[this] final val kafkaTopologySupplierMethodSignature = s"()$kafkaTopologySupplierFieldSignature"
  private[this] final val kafkaStreamsPackageName              = kafkaTopologyClass.getPackage.getName

  private[this] final def instantiate(className: String, cl: ClassLoader): (Class[_], AnyRef) = {
    val clazz = Class.forName(className, true, cl)
    clazz.getEnclosingClass()
    clazz -> clazz.getDeclaredConstructor().initialize
  }

  private[this] final def findTopologiesInClass(ci: ClassInfo, cl: ClassLoader): List[(String, AnyRef)] =
    if (ci.superClassName == kafkaTopologyClassName)
      (ci.name -> instantiate(ci.name, cl)._2) :: Nil
    else if (!ci.implements(supplierClassName)) {
      val fields           = ci.fields.filter(_.descriptor == kafkaTopologyFieldDescriptor)
      val methods          = ci.methods.filter(_.descriptor == kafkaTopologyMethodDescriptor)
      val function0Fields  = ci.fields.filter(_.signature == kafkaTopologyFun0FieldSignature)
      val function0Methods = ci.methods.filter(_.signature == kafkaTopologyFun0MethodSignature)
      val supplierFields   = ci.fields.filter(_.signature == kafkaTopologySupplierFieldSignature)
      val supplierMethods  = ci.methods.filter(_.signature == kafkaTopologySupplierMethodSignature)

      if (
        fields.nonEmpty || methods.nonEmpty || function0Fields.nonEmpty || function0Methods.nonEmpty || supplierFields.nonEmpty || supplierMethods.nonEmpty
      ) {
        val name              = ci.name
        val (clazz, instance) = instantiate(name, cl)

        val fs = fields.map { f =>
          f.name -> clazz.getDeclaredField(f.name).inside(instance)
        } ++ function0Fields.map { f =>
          val function0 = clazz.getDeclaredField(f.name).inside(instance)
          f.name -> function0.getClass.getDeclaredMethod("apply").inside(function0)
        } ++ supplierFields.map { f =>
          val supplier = clazz.getDeclaredField(f.name).inside(instance)
          f.name -> supplier.getClass.getDeclaredMethod("get").inside(supplier)
        }
        val ms = methods.map { m =>
          m.name -> clazz.getDeclaredMethod(m.name).inside(instance)
        } ++ function0Methods.map { m =>
          val function0 = clazz.getDeclaredMethod(m.name).inside(instance)
          m.name -> function0.getClass.getDeclaredMethod("apply").inside(function0)
        } ++ supplierMethods.map { m =>
          val supplier = clazz.getDeclaredMethod(m.name).inside(instance)
          m.name -> supplier.getClass.getDeclaredMethod("get").inside(supplier)
        }

        {
          fs.map { case (k, v) => s"$name#$k" -> v } ++
            ms.filterNot { case (n, _) => fs.exists(_._1 == n) }.map { case (k, v) => s"$name$$$k" -> v }
        }.toList
      } else Nil
    } else Nil

  private[this] final def checkTopology(instance: AnyRef): (Boolean, String) = {
    val topologyDescription = instance.getClass.getMethod("describe").inside(instance)
    val subtopologiesSet    = topologyDescription.getClass.getDeclaredMethod("subtopologies").inside(topologyDescription)
    !subtopologiesSet.getClass.getMethod("isEmpty").inside(subtopologiesSet).asInstanceOf[Boolean] -> topologyDescription.toString
  }

  final def dotsToSlashes(s: String) = dotRegex.replaceAllIn(s, "/")

  final def findTopologies(log: Logger, cl: ClassLoader) =
    (ci: ClassInfo) =>
      if (ci.name.startsWith(kafkaStreamsPackageName)) Nil
      else
        findTopologiesInClass(ci, cl).flatMap { case (name, instance) =>
          checkTopology(instance) match {
            case (true, topology) =>
              log.info(s"Eisner - found topology: $name")
              Some(name -> topology)
            case _ =>
              log.info(s"Eisner - skipping empty topology: $name")
              None
          }
        }
}
