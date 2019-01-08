package com.mesosphere.sdk.specification.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Raw Scheduler configuration.
 */
public final class RawScheduler {

  private final String principal;

    private final String principalSecret;

  private final String zookeeper;

    private final String zookeeperCredential;

    private final String zookeeperRootDir;

  private final String user;

  private RawScheduler(
          @JsonProperty("principal") String principal,
          @JsonProperty("principalSecret") String principalSecret,
          @JsonProperty("zookeeper") String zookeeper,
          @JsonProperty("zookeeperCredential") String zookeeperCredential,
          @JsonProperty("zookeeperRootDir") String zookeeperRootDir,
          @JsonProperty("user") String user)
  {
    this.principal = principal;
      this.principalSecret = principalSecret;
    this.zookeeper = zookeeper;
    this.user = user;
      this.zookeeperCredential = zookeeperCredential;
      this.zookeeperRootDir = zookeeperRootDir;
  }

  public String getPrincipal() {
    return principal;
  }

    public String getPrincipalSecret() {
        return principalSecret;
    }

  public String getZookeeper() {
    return zookeeper;
  }

  public String getUser() {
    return user;
  }

    public String getZookeeperCredential() {
        return zookeeperCredential;
    }

    public String getZookeeperRootDir() {
        return zookeeperRootDir;
    }
}
