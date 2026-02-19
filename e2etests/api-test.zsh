#!/usr/bin/env zsh

# ==============================================================================
# SCRIPT DE TESTING E2E (END-TO-END) PARA API REST
# ==============================================================================
# Descripción:
#   Ejecuta tests definidos en archivos YAML contra una API.
#   Gestiona autenticación (Token Bearer) y sesiones de usuario automáticamente.
#   Soporta sustitución de variables ({id}) y validación de status HTTP.
#
# Dependencias:
#   - curl: Para realizar las peticiones HTTP.
#   - jq:   Para procesar JSON y extraer tokens/IDs.
#   - yq:   Para leer los archivos de test en formato YAML.
# ==============================================================================

# --- 1. CONFIGURACIÓN VISUAL (Colores y Estilos) ---
# Definimos códigos ANSI para salida coloreada en terminal.
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
GRAY='\033[0;90m'
BOLD='\033[1m'
NC='\033[0m' # No Color (Resetea el color)

# Iconos para feedback visual rápido
ICON_PASS="✅"
ICON_FAIL="❌"
ICON_WARNING="⚠️"
ICON_ROCKET="🚀"
ICON_GEAR="⚙️"
ICON_KEY="🔑"
ICON_SAVE="💾"
ICON_VAR="📦"

# --- 2. CONFIGURACIÓN DE ENTORNO ---
# Inicialización de variables globales
OPTIND=1
BASE_URL="http://localhost:8080"  # URL por defecto
SHOW_HEADER=true                  # Mostrar cabecera de cada test
VERBOSE=false                     # Modo detallado (muestra JSON responses)
START_TIME=$(date +%s)            # Timestamp de inicio para calcular duración
SESSION_FILE="/tmp/stockman_e2e_session.json" # Archivo temporal para persistencia

# Contadores para el resumen final
TOTAL_TESTS=0
PASSED_COUNT=0
FAILED_COUNT=0

# --- 3. VERIFICACIÓN DE DEPENDENCIAS ---
# Es buena práctica asegurar que las herramientas necesarias existen antes de empezar.
for cmd in curl jq yq; do
  if ! command -v $cmd &> /dev/null; then
      echo -e "${RED}${ICON_FAIL} Error Crítico: '$cmd' no está instalado.${NC}"
      exit 1
  fi
done

# ==============================================================================
# SECCIÓN: GESTIÓN DE SESIÓN (Persistencia temporal)
# ==============================================================================

# Inicializa el archivo de sesión si no existe
init_session() {
  if [[ ! -f "$SESSION_FILE" ]]; then
    echo '{"token": "", "variables": {}}' > "$SESSION_FILE"
  fi
}

# Obtiene el token
get_token() {
  init_session
  jq -r '.token // empty' "$SESSION_FILE"
}

# Guarda el token
save_token() {
  local token=$1
  init_session
  local temp=$(cat "$SESSION_FILE")
  if [[ -z "$token" ]]; then
    echo "$temp" | jq '.token = null' > "$SESSION_FILE"
  else
    echo "$temp" | jq --arg v "$token" '.token = $v' > "$SESSION_FILE"
  fi
}

# Guarda una variable
save_variable() {
  local key=$1
  local value=$2
  init_session
  local temp=$(cat "$SESSION_FILE")
  echo "$temp" | jq --arg k "$key" --arg v "$value" '.variables[$k] = $v' > "$SESSION_FILE"
}

# Inyecta variables en un texto
inject_variables () {
  local text="$1"
  init_session
  #Extraemos las variables en formato clave=valor
  local VARS=$(jq -r '.variables | to_entries | .[] | "\(.key)=\(.value)"' "$SESSION_FILE" 2>/dev/null)
  if [[ -n "$VARS" ]]; then
    while IFS= read -r var_line; do
      [[ -z "$var_line" ]] && continue
      local key="${var_line%%=*}"
      local value="${var_line#*=}"
      text="${text//\{\{$key\}\}/$value}"
    done <<< "$VARS"
  fi
  echo "$text"
}

# ==============================================================================
# SECCIÓN: PROCESAMIENTO DE ARGUMENTOS
# ==============================================================================
while getopts ":nvu:" opt; do
  case $opt in
    n) SHOW_HEADER=false ;;      # -n: No header
    v) VERBOSE=true ;;           # -v: Verbose mode
    u) BASE_URL=${OPTARG%/} ;;   # -u: Custom URL
    \?) echo -e "${RED}Opción inválida: -$OPTARG${NC}" >&2; exit 1 ;;
  esac
done
shift $((OPTIND -1))


# ==============================================================================
# SECCIÓN: NÚCLEO DEL TEST (run_test)
# ==============================================================================

run_test() {
    local FILE=$1
    # Si el archivo no existe, saltamos
    [[ ! -f "$FILE" ]] && return

    ((TOTAL_TESTS++))
    
    # ---------------------------------------------------------
    # A. Manejo de Logout (Acción especial)
    # ---------------------------------------------------------
    local IS_LOGOUT=$(yq eval '.logout // "false"' "$FILE")
    if [[ "$IS_LOGOUT" == "true" ]]; then
      save_token ""
      echo -e "${GRAY}------------------------------------------------------------${NC}"
      echo -e "${GREEN}${BOLD}${ICON_PASS} LOGOUT (Token eliminado)${NC}"
      echo -e "\n"
      ((PASSED_COUNT++))
      return
    fi

    # ---------------------------------------------------------
    # B. Lectura y Preparación de Datos
    # ---------------------------------------------------------
    local METHOD=$(yq eval '.method' "$FILE")
    local ENDPOINT_RAW=$(yq eval '.endpoint' "$FILE")
    local EXPECTED=$(yq eval '.expected // ""' "$FILE")
    
    # Lectura del Body. Si es null, enviamos JSON vacío {}
    local BODY_RAW=$(yq eval '.body' "$FILE" -o=json -I=0)
    [[ "$BODY_RAW" == "null" ]] && BODY_RAW="{}"

    # Inyección de variables
    local ENDPOINT=$(inject_variables "$ENDPOINT_RAW")
    local BODY=$(inject_variables "$BODY_RAW")

    # ---------------------------------------------------------
    # D. Interfaz de Usuario (Header)
    # ---------------------------------------------------------
    if $SHOW_HEADER; then
      echo -e "${GRAY}------------------------------------------------------------${NC}"
      echo -e "${BOLD}${BLUE}TEST: ${NC}$(basename "$FILE" .yml)"
      
      # Si hubo reemplazo de variables, mostramos la URL final y la original
      if [[ "$ENDPOINT" != "$ENDPOINT_RAW" ]]; then
         echo -e "${YELLOW}${ICON_ROCKET} ${METHOD} ${NC}${BASE_URL}${ENDPOINT} ${GRAY}(Variables inyectadas)${NC}"
      else
         echo -e "${YELLOW}${ICON_ROCKET} ${METHOD} ${NC}${BASE_URL}${ENDPOINT}"
      fi
    fi

    # ---------------------------------------------------------
    # E. Preparación de Cabeceras
    # ---------------------------------------------------------
    local API_TOKEN=$(get_token)
    local AUTH_HEADER=""
    [[ -n "$API_TOKEN" ]] && AUTH_HEADER="Authorization: Bearer $API_TOKEN"

    # ---------------------------------------------------------
    # F. Ejecución de cURL
    # ---------------------------------------------------------
    # Usamos un separador único para distinguir el Body del HTTP Code en la salida
    local SEPARATOR="||_HTTP_CODE_||"
    
    RESPONSE_RAW=$(curl -s -i -X "$METHOD" "$BASE_URL$ENDPOINT" \
        -H "Content-Type: application/json" \
        -H "$AUTH_HEADER" \
        -d "$BODY" \
        -w "${SEPARATOR}%{http_code}") # -w escribe el código al final

    # ---------------------------------------------------------
    # G. Procesamiento de la Respuesta
    # ---------------------------------------------------------
    # Dividimos la respuesta cruda usando el separador
    local HTTP_BODY_AND_HEADERS="${RESPONSE_RAW%%$SEPARATOR*}"
    local HTTP_STATUS="${RESPONSE_RAW##*$SEPARATOR}"

    # Mostrar info extra si estamos en modo Verbose o Header
    if [[ "$SHOW_HEADER" == "true" || "$VERBOSE" == "true" ]]; then
      echo -e "${CYAN}--- HTTP STATUS: $HTTP_STATUS ---${NC}"
      
      if [[ "$VERBOSE" == "true" ]]; then
         # Extraemos solo el cuerpo JSON para mostrarlo bonito con jq
         # awk busca el primer salto de línea doble (\r\n\r\n) que separa headers de body
         local JSON_BODY=$(echo "$HTTP_BODY_AND_HEADERS" | awk 'BEGIN{RS="\r\n\r\n"} NR>1{print}')
         
         if [[ -n "$JSON_BODY" ]]; then
             echo "$JSON_BODY" | jq -C . 2>/dev/null || echo "$JSON_BODY"
         fi
      fi
    fi

    # ---------------------------------------------------------
    # H. Validación (Pass/Fail)
    # ---------------------------------------------------------
    local PASSED=false
    # Si hay EXPECTED, comparamos exacto. Si no, cualquier 2xx es válido.
    if [[ ( -n "$EXPECTED" && "$HTTP_STATUS" == "$EXPECTED" ) || ( -z "$EXPECTED" && "$HTTP_STATUS" =~ ^2 ) ]]; then
      PASSED=true
    fi

    if [[ "$PASSED" == "true" ]]; then
      ((PASSED_COUNT++))
      echo -e "${GREEN}${BOLD}${ICON_PASS} PASSED${NC}"
        
        # -----------------------------------------------------
        # I. Captura de Datos (Token / User ID)
        # -----------------------------------------------------
        # Limpiamos headers, quedándonos solo con el JSON body
        local JSON_BODY=$(echo "$HTTP_BODY_AND_HEADERS" | sed '1,/^\r$/d')

        # FIX IMPORTANTE: Usamos 'objects |' en jq.
        # Si la respuesta es una lista array [...], 'objects' filtrará y evitará
        # el error "Cannot index array". Si es un objeto, buscará las claves.
        
        local NEW_TOKEN=$(echo "$JSON_BODY" | jq -r 'objects | (.token // .accessToken // empty)' 2>/dev/null)

        if [[ -n "$NEW_TOKEN" ]]; then
            save_token "$NEW_TOKEN"
            echo -e "${GREEN}${ICON_SAVE} Token almacenado${NC}"
        fi

        # -----------------------------------------------------
        # Almacenamiento de variables múltiples (Diccionario):
        # -----------------------------------------------------
        
        # 1. Comprobamos si la clave 'store' existe y no es nula ANTES de iterar
        local HAS_STORE=$(yq eval 'has("store")' "$FILE" 2>/dev/null)
        
        if [[ "$HAS_STORE" == "true" ]]; then
            # Ahora es seguro iterar porque sabemos que 'store' existe
            local STORE_ENTRIES=$(yq eval '.store | to_entries | .[] | "\(.key)=\(.value)"' "$FILE" 2>/dev/null)
            
            if [[ -n "$STORE_ENTRIES" ]]; then
                while IFS= read -r store_line; do
                    # Evitamos procesar líneas vacías o extrañas
                    [[ -z "$store_line" || "$store_line" == "=" ]] && continue
                    
                    local STORE_VAR="${store_line%%=*}"
                    local STORE_FIELD="${store_line#*=}"
                    
                    # Extraer del JSON de respuesta
                    local EXTRACTED_VAL=$(echo "$JSON_BODY" | jq -r "objects | .$STORE_FIELD // empty" 2>/dev/null)
                    
                    if [[ -n "$EXTRACTED_VAL" ]]; then
                        save_variable "$STORE_VAR" "$EXTRACTED_VAL"
                        echo -e "${GREEN}${ICON_VAR} Guardado ${BOLD}$STORE_VAR${NC} = $EXTRACTED_VAL"
                    else
                        echo -e "${RED}${ICON_WARNING} No se encontró el campo '$STORE_FIELD' para '$STORE_VAR'${NC}"
                    fi
                done <<< "$STORE_ENTRIES"
            fi
        fi

    else
      ((FAILED_COUNT++))
      echo -e "\n${RED}${BOLD}${ICON_FAIL} FAILED${NC} (Esperado: ${EXPECTED:-2xx}, Recibido: $HTTP_STATUS)"
      
      # Si falla, mostramos el cuerpo para debug rápido (incluso sin -v)
      if [[ "$VERBOSE" == "false" ]]; then
          echo -e "${GRAY}Respuesta: $(echo "$HTTP_BODY_AND_HEADERS" | awk 'BEGIN{RS="\r\n\r\n"} NR>1{print}')${NC}"
      fi
    fi
    echo -e "\n"
}

# ==============================================================================
# SECCIÓN: EJECUCIÓN PRINCIPAL
# ==============================================================================

# Validar que hay argumentos
if [[ $# -eq 0 ]]; then
    echo -e "${RED}Uso: $0 tests/*.yml${NC}"
    exit 1
fi

if [[ $SHOW_HEADER == "true" ]]; then
    echo -e "${CYAN}${BOLD}${ICON_GEAR} Entorno Objetivo: ${NC}${BASE_URL}"
fi

# Bucle principal: Iterar sobre todos los archivos pasados como argumento
for test_file in "$@"; do
    run_test "$test_file"
done

# Resumen Final
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo -e "${GRAY}------------------------------------------------------------${NC}"
echo -e "${BOLD}📊 RESUMEN DE EJECUCIÓN:${NC}"
echo -e "   ${GREEN}✅ PASSED: $PASSED_COUNT${NC}"
echo -e "   ${RED}❌ FAILED: $FAILED_COUNT${NC}"
echo -e "   ${BLUE}⏱️  TIEMPO: ${DURATION}s${NC}"
echo -e "${GRAY}------------------------------------------------------------${NC}"

# Código de salida del script (0 si todo OK, 1 si hubo fallos)
[[ $FAILED_COUNT -gt 0 ]] && exit 1 || exit 0
