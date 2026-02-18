package com.marckux.stockman.auth.application.ports.in.usecases;

@FunctionalInterface
public interface UseCase<T, R> {

  /**
   * Ejecuta la lógica de un caso de uso
   * @param input Datos de entrada
   * @return Resultado de la ejecución
   */
  R execute(T input);
}
