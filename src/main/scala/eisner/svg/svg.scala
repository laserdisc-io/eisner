package eisner

import io.circe.generic.extras.Configuration

package object svg {
  implicit final val configuration: Configuration = Configuration.default.withDiscriminator("type")
}
