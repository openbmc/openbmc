SUMMARY = "Zaius board wiring"
DESCRIPTION = "Board wiring information for the Zaius OpenPOWER system."
PR = "r1"

inherit config-in-skeleton

PROVIDES_remove = "virtual/obmc-inventory-data"
RPROVIDES_${PN}_remove = "virtual-obmc-inventory-data"
