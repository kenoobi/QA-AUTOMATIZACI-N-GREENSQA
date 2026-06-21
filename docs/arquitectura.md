# Arquitectura y estrategia de pruebas

## Capas

`CLI -> TestDataService -> Generator / Repository / CSV / Notifier`

La capa de dominio no conoce H2, SMTP, CSV ni Serenity. Las dependencias apuntan a contratos pequeños para permitir reemplazar cada adaptador.

En UI se usa `Feature -> Step Definitions -> Action Class -> Page Object`. El Page Object es el único lugar que conoce la estructura cambiante de LATAM; las acciones expresan intención de negocio y Gherkin describe casos revisables por negocio.

## Criterios de los tres casos

| Caso | Riesgo cubierto | Datos | Resultado esperado |
|---|---|---|---|
| Nacional ida/vuelta | Flujo principal y dos fechas | Origen del viajero + CTG | Lista de ofertas |
| Internacional solo ida | Cambio de tipo de viaje | Origen del viajero + MIA | Lista de ofertas |
| Internacional ida/vuelta | Segunda ruta y regresión | Origen del viajero + LIM | Lista de ofertas |

## Enfoque Shift Right

Los reportes Serenity, capturas en fallo y datos asociados al escenario facilitan observar fallos en ejecución continua. En CI conviene registrar duración, tasa de error por ruta y cambios de DOM, y ejecutar contra producción con baja frecuencia y sin completar una compra.
