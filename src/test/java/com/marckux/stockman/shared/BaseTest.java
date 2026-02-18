package com.marckux.stockman.shared;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

  // Instanciamos el logger manualmente para la clase estática interna o usamos Lombok si prefieres
  // Aquí uso LoggerFactory para que sea explícito y no dependa de Lombok en la clase interna
  private static final Logger log = LoggerFactory.getLogger(PrettyConsoleLogger.class);

  @RegisterExtension
  static PrettyConsoleLogger logger = new PrettyConsoleLogger();

  public static class PrettyConsoleLogger
      implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    // --- CÓDIGOS ANSI PARA COLORES ---
    private static final String RESET = "\u001B[0m";
    private static final String RED_BOLD = "\u001B[1;31m";    // Rojo Negrita
    private static final String GREEN_BOLD = "\u001B[1;32m";  // Verde Negrita
    private static final String BLUE_BOLD = "\u001B[1;34m";   // Azul Negrita
    private static final String CYAN = "\u001B[36m";          // Cyan

    // --- PREFIJO PARA GREP ---
    // Usamos un prefijo único. Al hacer grep, filtraremos por esto.
    private static final String LOG_TAG = "\n[TEST_RESULT]";

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
      String className = context.getRequiredTestClass().getSimpleName();
      String displayName = context.getDisplayName();

      // Formato: [TAG] 🔵 Clase > Test
      // Usamos log.info. Los colores van incrustados en los argumentos.
      log.info("{} {}🔵 {} > {}{}", 
          LOG_TAG, 
          BLUE_BOLD, 
          className, 
          displayName, 
          RESET);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
      Optional<Throwable> exception = context.getExecutionException();

      if (exception.isPresent()) {
        // ❌ FALLÓ (En Rojo y Negrita)
        log.error("{} {}❌ FALLÓ └── Causa: {}{}", 
            LOG_TAG, 
            RED_BOLD, 
            exception.get().getMessage(), 
            RESET);
      } else {
        // ✅ OK (En Verde y Negrita)
        log.info("{} {}✅ OK{}", 
            LOG_TAG, 
            GREEN_BOLD, 
            RESET);
      }
    }
  }
}
//package com.marckux.stockman.shared
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
//import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.junit.jupiter.api.extension.RegisterExtension;
//
//public class BaseTest {
//
//  @RegisterExtension
//  static PrettyConsoleLogger logger = new PrettyConsoleLogger();
//
//  public static class PrettyConsoleLogger
//      implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
//
//    @Override
//    public void afterTestExecution(ExtensionContext context) throws Exception {
//      Optional<Throwable> exception = context.getExecutionException();
//      if (exception.isPresent()) {
//        // ❌ FALLÓ (con mensaje)
//        System.out.printf(" ❌ FALLÓ \n    └── %s%n", exception.get().getMessage());
//      } else {
//        // ✅ OK
//        System.out.print(" ✅ OK");
//      }
//    }
//
//    @Override
//    public void beforeTestExecution(ExtensionContext context) throws Exception {
//      String className = context.getRequiredTestClass().getSimpleName();
//      String displayName = context.getDisplayName();
//      System.out.printf("\n🔵 %s > %s", className, displayName);
//    }
//
//  }
//}
