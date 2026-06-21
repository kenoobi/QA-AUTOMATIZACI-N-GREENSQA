# language: es
Característica: Búsqueda de vuelos con datos ficticios
  Como equipo de aseguramiento de calidad de LATAM
  Quiero buscar vuelos usando viajeros generados por la solución
  Para validar el flujo principal de venta

  Antecedentes:
    Dado un viajero generado y persistido como dato de entrada
    Y el viajero abre el buscador de LATAM

  @smoke @roundtrip
  Escenario: Buscar un vuelo nacional de ida y vuelta
    Cuando busca un vuelo "Ida y vuelta" hacia "CTG" para dentro de 30 días con regreso en 37 días
    Entonces se presentan los resultados de vuelos

  @smoke @oneway
  Escenario: Buscar un vuelo internacional de solo ida
    Cuando busca un vuelo "Solo ida" hacia "MIA" para dentro de 35 días
    Entonces se presentan los resultados de vuelos

  @regression @roundtrip
  Escenario: Buscar un segundo destino internacional de ida y vuelta
    Cuando busca un vuelo "Ida y vuelta" hacia "LIM" para dentro de 40 días con regreso en 48 días
    Entonces se presentan los resultados de vuelos
