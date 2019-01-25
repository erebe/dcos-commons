package com.mesosphere.sdk.consulkv.scheduler;

import com.mesosphere.sdk.scheduler.plan.Phase;
import com.mesosphere.sdk.scheduler.plan.Plan;
import com.mesosphere.sdk.scheduler.plan.PodInstanceRequirement;
import com.mesosphere.sdk.scheduler.recovery.RecoveryPlanOverrider;
import com.mesosphere.sdk.state.StateStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The CassandraRecoveryPlanManager handles failure scenarios unique to Cassandra. It falls back to the default recovery
 * behavior when appropriate.
 */
public class RecoveryOverrider implements RecoveryPlanOverrider {
  private static final String RECOVERY_PHASE_NAME = "permanent-node-failure-recovery";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final StateStore stateStore;

  private final Plan replaceNodePlan;

  RecoveryOverrider(StateStore stateStore, Plan replaceNodePlan) {
    this.stateStore = stateStore;
    this.replaceNodePlan = replaceNodePlan;
  }

  @Override
  public Optional<Phase> override(PodInstanceRequirement stoppedPod) {

    return Optional.empty();
    //Optional.ofNullable(new DefaultPhase("noop", Collections.emptyList(),
    // new SerialStrategy<>(), Collections.emptyList()));

  }
}
