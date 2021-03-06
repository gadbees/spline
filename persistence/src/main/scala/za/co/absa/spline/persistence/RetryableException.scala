/*
 * Copyright 2019 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.spline.persistence

import java.util.concurrent.CompletionException

import com.arangodb.ArangoDBException
import za.co.absa.spline.persistence.ArangoCode._

object RetryableException {

  private[persistence] val RetryableCodes = Set(
    ArangoConflict,
    ArangoUniqueConstraintViolated,
    Deadlock,
    ArangoSyncTimeout,
    LockTimeout,
    ArangoWriteThrottleTimeout,
    ClusterTimeout)
    .map(_.code)

  def unapply(exception: Throwable): Option[RuntimeException] = exception match {
    case e: ArangoDBException if RetryableCodes(e.getResponseCode) => Some(e)
    case e: CompletionException => Option(e.getCause).flatMap(unapply).map(_ => e)
    case _ => None
  }

}
