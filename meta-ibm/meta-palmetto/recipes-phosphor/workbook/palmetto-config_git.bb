SUMMARY = "Palmetto board wiring"
DESCRIPTION = "Board wiring information for the Palmetto system."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit config-in-skeleton

PROVIDES_remove = "virtual/obmc-inventory-data"
RPROVIDES_${PN}_remove = "virtual-obmc-inventory-data"
