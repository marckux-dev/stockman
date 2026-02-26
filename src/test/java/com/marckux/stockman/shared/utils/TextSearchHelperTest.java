package com.marckux.stockman.shared.utils;

import com.marckux.stockman.shared.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests para verificar el formateo de textos optimizados para búsqueda.
 */
class TextSearchHelperTest extends BaseTest {

    @Test
    @DisplayName("Debería devolver vacío cuando el texto es nulo")
    void shouldReturnEmptyWhenNull() {
        // El helper está definido para retornar cadena vacía cuando la entrada es null.
        assertEquals("", TextSearchHelper.formatForSearch(null));
    }

    @ParameterizedTest
    @DisplayName("Debería eliminar diacríticos, convertir a minúsculas y normalizar espacios")
    @MethodSource("normalizeExamples")
    void shouldNormalizeTextForSearch(String input, String expected) {
        // Verifica que la salida quede limpia y apta para búsquedas.
        assertEquals(expected, TextSearchHelper.formatForSearch(input));
    }

    static Iterable<Arguments> normalizeExamples() {
        return java.util.List.of(
                // Tildes y mayúsculas
                Arguments.of("Árbol de NAVIDAD", "arbol de navidad"),
                // Ñ -> n y diéresis
                Arguments.of("pingüino y mañana", "pinguino y manana"),
                // Acentos en distintas vocales
                Arguments.of("PÚBLICO ELÉCTRICO ÍGNEO", "publico electrico igneo"),
                // Diacríticos combinados
                Arguments.of("Crème brûlée", "creme brulee"),
                // Tildes en español
                Arguments.of("Canción del corazón", "cancion del corazon"),
                // Múltiples espacios
                Arguments.of("  Hola   mundo  ", "hola mundo"),
                // Mezcla de tabs y saltos de línea
                Arguments.of("Hola\t\nMundo", "hola mundo"),
                // Caracteres especiales sustituidos por espacios
                Arguments.of("Calle/Av.*# + 123", "calle av 123"),
                // Barras invertidas y guiones
                Arguments.of("Ruta\\Servidor - API", "ruta servidor api"),
                // Símbolos varios
                Arguments.of("Precio: 50€ + IVA", "precio 50 iva"),
                // Mantener números y ordinals
                Arguments.of("Lote Nº 10", "lote no 10"),
                // Comillas y paréntesis
                Arguments.of("(Ejemplo) \"Texto\" ", "ejemplo texto"),
                // Mezcla de signos
                Arguments.of("a+b*c/d\\e", "a b c d e"),
                // Solo símbolos
                Arguments.of("***///\\\\", "")
        );
    }

    @Test
    @DisplayName("Debería convertir caracteres especiales a espacios y recortar")
    void shouldReplaceSpecialCharsWithSpacesAndTrim() {
        // Separadores y símbolos deben convertirse en espacios y colapsarse.
        assertEquals("alpha beta gamma", TextSearchHelper.formatForSearch("  alpha+beta///gamma  "));
    }

    @Test
    @DisplayName("Debería dejar una cadena vacía si todo eran símbolos")
    void shouldReturnEmptyWhenOnlySymbols() {
        // Al eliminar símbolos, la cadena final queda vacía.
        assertEquals("", TextSearchHelper.formatForSearch("***///\\\\+++"));
    }
}
