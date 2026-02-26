package com.marckux.stockman.stock.application.ports.in.usecases;

/**
 * Contrato base para casos de uso.
 */
@FunctionalInterface
public interface UseCase<T, R> {

  /**
   * Ejecuta la lógica del caso de uso.
   * @param input Datos de entrada.
   * @return Resultado de la ejecución.
   */
  R execute(T input);
}
