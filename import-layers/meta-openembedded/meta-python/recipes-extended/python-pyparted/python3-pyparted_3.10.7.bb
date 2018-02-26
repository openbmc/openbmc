require python-pyparted.inc

DEPENDS += "python3-re"

PV = "3.10.7+git${SRCPV}"

inherit distutils3

RDEPENDS_${PN} += "python3-stringold python3-codecs python3-math"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
