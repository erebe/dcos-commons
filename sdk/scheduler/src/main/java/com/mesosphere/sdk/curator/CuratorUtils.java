package com.mesosphere.sdk.curator;

import com.mesosphere.sdk.scheduler.SchedulerUtils;
import com.mesosphere.sdk.storage.Persister;
import com.mesosphere.sdk.storage.PersisterException;
import com.mesosphere.sdk.storage.StorageError.Reason;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A set of common utilites for managing Curator/Zookeeper paths and data.
 */
public final class CuratorUtils {

  private static final int DEFAULT_CURATOR_POLL_DELAY_MS = 1000;

  private static final int DEFAULT_CURATOR_MAX_RETRIES = 3;

  /**
   * Name to use for storing a reverse mapping of the service name. This is lowercased as it's
   * something that's being handled in the underlying Persister layer (like {@link CuratorLocker}'s
   * "lock"), whereas common storage handling in the {@code storage} package is PascalCased.
   */
  private static final String SERVICE_NAME_NODE = "servicename";

  /**
   * This must never change, as it affects the path to the SchemaVersion object for a given
   * framework name.
   */
  private static final String SERVICE_ROOT_PATH_PREFIX = "/";

  /**
   * This must never change, as it affects the serialization of the ServiceName node.
   */
  private static final Charset SERVICE_NAME_CHARSET = StandardCharsets.UTF_8;

  private CuratorUtils() {
    // do not instantiate
  }

  /**
   * Returns the root node to store all scheduler ZK data inside. For example:
   *
   * <ul>
   * <li>"your-name-here" => /dcos-service-your-name-here</li>
   * <li>"/path/to/your-name-here" => /dcos-service-path__to__your-name-here</li>
   * </ul>
   */
  public static String getServiceRootPath(String frameworkName) {
    // /dcos-service-<your__name__here>
    return SERVICE_ROOT_PATH_PREFIX + SchedulerUtils.withEscapedSlashes(frameworkName);
  }

  /**
   * Returns a reasonable default retry policy for querying ZK.
   */
  static RetryPolicy getDefaultRetry() {
    return new ExponentialBackoffRetry(
        CuratorUtils.DEFAULT_CURATOR_POLL_DELAY_MS, CuratorUtils.DEFAULT_CURATOR_MAX_RETRIES);
  }

  /**
   * Compares the service name to the previously stored name in zookeeper, or creates a new node
   * containing this data if it isn't already present. This is useful for two situations where
   * foldered service names may be confused with literal double-underscore delimited names:
   *
   * <ul>
   * <li>
   * Protecting against collisions if someone uses periods in their service names. For example,
   * a foldered service named "/myteam/database" could collide with a non-foldered service named
   * "myteam.database" as the prior is converted to the latter where ZK is concerned.
   * </li>
   * <li>
   * Allowing deterministic mapping of a given ZK node back to the originating service.
   * For example, the data in "dcos-service-myteam.database" could be for a service named "myteam
   * .database", or for a foldered service named "/myteam/database". Storing the name explicitly
   * allows potential tooling to tell which one it is. This isn't currently needed but could be
   * useful someday.
   * </li>
   * </ul>
   *
   * @param persister   the persister where the service name should be written
   * @param serviceName the service name to check for equality, or to write if no service name
   *                    node exists
   * @see CuratorUtils#getServiceRootPath(String)
   */
  static void initServiceName(Persister persister, String serviceName) {
    try {
      String currentServiceName = getServiceName(persister);
      if (!currentServiceName.equals(serviceName)) {
        throw new IllegalArgumentException(String.format(
            "Collision between similar service names: Expected name '%s', but stored name is '%s'.",
            serviceName, currentServiceName));
      }
    } catch (PersisterException e) {
      if (e.getReason() == Reason.NOT_FOUND) {
        // The service name doesn't exist yet, either due to a new install or an upgrade from a
        // prior version that doesn't store this information. Initialize.
        try {
          persister.set(SERVICE_NAME_NODE, serviceName.getBytes(SERVICE_NAME_CHARSET));
        } catch (PersisterException e2) {
          throw new IllegalStateException("Failed to store service name", e2);
        }
      } else {
        throw new IllegalStateException("Failed to fetch prior service name for validation", e);
      }
    }
  }

  private static String getServiceName(Persister persister) throws PersisterException {
    byte[] bytes = persister.get(SERVICE_NAME_NODE);
    if (bytes.length == 0) {
      throw new IllegalArgumentException(String.format(
          "Invalid data when fetching service name in '%s'", SERVICE_NAME_NODE));
    }
    return new String(bytes, SERVICE_NAME_CHARSET);
  }
}
