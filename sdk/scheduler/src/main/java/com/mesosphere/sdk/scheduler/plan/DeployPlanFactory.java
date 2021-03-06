package com.mesosphere.sdk.scheduler.plan;

import com.mesosphere.sdk.offer.Constants;
import com.mesosphere.sdk.scheduler.plan.strategy.SerialStrategy;
import com.mesosphere.sdk.scheduler.plan.strategy.Strategy;
import com.mesosphere.sdk.scheduler.plan.strategy.StrategyGenerator;
import com.mesosphere.sdk.specification.ServiceSpec;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Given a PhaseFactory and a StrategyGenerator for the Phases, the DeployPlanFactory generates a Plan.
 */
public class DeployPlanFactory implements PlanFactory {
  private final StrategyGenerator<Phase> strategyGenerator;

  private final PhaseFactory phaseFactory;

  public DeployPlanFactory(PhaseFactory phaseFactory) {
    this(phaseFactory, new SerialStrategy.Generator<>());
  }

  public DeployPlanFactory(PhaseFactory phaseFactory, StrategyGenerator<Phase> strategyGenerator) {
    this.phaseFactory = phaseFactory;
    this.strategyGenerator = strategyGenerator;
  }

  public static Plan getPlan(String name, List<Phase> phases, Strategy<Phase> strategy) {
    return getPlan(name, phases, strategy, Collections.emptyList());
  }

  public static Plan getPlan(
      String name,
      List<Phase> phases,
      Strategy<Phase> strategy,
      List<String> errors)
  {
    return new DefaultPlan(name, phases, strategy, errors);
  }

  @Override
  public Plan getPlan(ServiceSpec serviceSpec) {
    List<Phase> phases = serviceSpec.getPods().stream()
        .map(phaseFactory::getPhase)
        .collect(Collectors.toList());
    return new DefaultPlan(Constants.DEPLOY_PLAN_NAME, phases, strategyGenerator.generate(phases));
  }
}
