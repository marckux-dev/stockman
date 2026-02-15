package com.marckux.stockman.shared;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;

public class BaseTest {

  @RegisterExtension
  static PrettyConsoleLogger logger = new PrettyConsoleLogger();

  public static class PrettyConsoleLogger
      implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
      Optional<Throwable> exception = context.getExecutionException();
      if (exception.isPresent()) {
        // ❌ FALLÓ (con mensaje)
        System.out.printf(" ❌ FALLÓ \n    └── %s%n", exception.get().getMessage());
      } else {
        // ✅ OK
        System.out.print(" ✅ OK");
      }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
      String className = context.getRequiredTestClass().getSimpleName();
      String displayName = context.getDisplayName();
      System.out.printf("\n🔵 %s > %s", className, displayName);
    }

  }
}
