package com.mesosphere.sdk.consulkv.scheduler;

import com.mesosphere.sdk.scheduler.plan.Plan;
import com.mesosphere.sdk.scheduler.recovery.RecoveryPlanOverriderFactory;
import com.mesosphere.sdk.state.StateStore;

import java.util.Collection;

/**
 * This class generates {@link RecoveryOverrider}s.
 */
public class RecoveryOverriderFactory implements RecoveryPlanOverriderFactory {
  private static final String REPLACE_PLAN_NAME = "replace";

  @Override
  public com.mesosphere.sdk.scheduler.recovery.RecoveryPlanOverrider create(StateStore stateStore, Collection<Plan> plans) {
    return new RecoveryOverrider(stateStore, plans.stream().findFirst().get());
  }

}
