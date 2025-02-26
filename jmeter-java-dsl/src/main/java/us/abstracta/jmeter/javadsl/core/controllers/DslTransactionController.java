package us.abstracta.jmeter.javadsl.core.controllers;

import java.lang.reflect.Method;
import java.util.List;
import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.control.gui.TransactionControllerGui;
import org.apache.jmeter.testelement.TestElement;
import us.abstracta.jmeter.javadsl.codegeneration.MethodCall;
import us.abstracta.jmeter.javadsl.codegeneration.MethodCallContext;
import us.abstracta.jmeter.javadsl.codegeneration.SingleTestElementCallBuilder;
import us.abstracta.jmeter.javadsl.codegeneration.TestElementParamBuilder;
import us.abstracta.jmeter.javadsl.codegeneration.params.ChildrenParam;
import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup.ThreadGroupChild;

/**
 * Allows specifying JMeter transaction controllers which group different samples associated to same
 * transaction.
 * <p>
 * This is usually used when grouping different steps of a flow, for example group requests of login
 * flow, adding item to cart, purchase, etc. It provides aggregate metrics of all it's samples.
 *
 * @since 0.14
 */
public class DslTransactionController extends BaseController<DslTransactionController> {

  protected boolean includeTimers = false;
  protected boolean generateParentSample = false;

  public DslTransactionController(String name, List<ThreadGroupChild> children) {
    super(name, TransactionControllerGui.class, children);
  }

  /**
   * Specifies to include time spent in timers and pre- and post-processors in sample results.
   *
   * @return the controller for further configuration or usage.
   * @since 1.0
   */
  public DslTransactionController includeTimersAndProcessorsTime() {
    return includeTimersAndProcessorsTime(true);
  }

  /**
   * Same as {@link #includeTimersAndProcessorsTime()} but allowing to enable or disable it.
   * <p>
   * This is helpful when the resolution is taken at runtime.
   *
   * @param enable specifies to enable or disable the setting. By default, it is set to false.
   * @return the controller for further configuration or usage.
   * @see #includeTimersAndProcessorsTime()
   * @since 0.29
   */
  public DslTransactionController includeTimersAndProcessorsTime(boolean enable) {
    includeTimers = enable;
    return this;
  }

  /**
   * Specifies to create a sample result as parent of children samplers.
   * <p>
   * It is useful in some scenarios to get transaction sample results as parent of children samplers
   * to focus mainly in transactions and not be concerned about each particular request. Enabling
   * parent sampler helps in this regard, only reporting the transactions in summary reports, and
   * not the transaction children results.
   *
   * @return the controller for further configuration or usage.
   * @since 1.0
   */
  public DslTransactionController generateParentSample() {
    return generateParentSample(true);
  }

  /**
   * Same as {@link #generateParentSample()} but allowing to enable or disable it.
   * <p>
   * This is helpful when the resolution is taken at runtime.
   *
   * @param enable specifies to enable or disable the setting. By default, it is set to false.
   * @return the controller for further configuration or usage.
   * @see #generateParentSample()
   * @since 0.29
   */
  public DslTransactionController generateParentSample(boolean enable) {
    generateParentSample = enable;
    return this;
  }

  @Override
  protected TestElement buildTestElement() {
    TransactionController ret = new TransactionController();
    ret.setGenerateParentSample(generateParentSample);
    // we can't use setIncludeTimers since it ignores true values
    ret.setProperty("TransactionController.includeTimers", includeTimers);
    return ret;
  }

  public static class CodeBuilder extends SingleTestElementCallBuilder<TransactionController> {

    public CodeBuilder(List<Method> builderMethods) {
      super(TransactionController.class, builderMethods);
    }

    @Override
    protected MethodCall buildMethodCall(TransactionController testElement,
        MethodCallContext context) {
      TestElementParamBuilder paramBuilder = new TestElementParamBuilder(testElement,
          "TransactionController");
      return buildMethodCall(paramBuilder.nameParam(null),
          new ChildrenParam<>(ThreadGroupChild[].class))
          .chain("generateParentSample", paramBuilder.boolParam("parent", false))
          .chain("includeTimersAndProcessorsTime", paramBuilder.boolParam("includeTimers", false));
    }

  }

}
