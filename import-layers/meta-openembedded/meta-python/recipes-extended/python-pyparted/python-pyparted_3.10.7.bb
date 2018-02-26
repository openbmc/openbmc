require python-pyparted.inc

DEPENDS += "python-re"

PV = "3.10.7+git${SRCPV}"

inherit distutils

RDEPENDS_${PN} += "python-stringold python-codecs python-math"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
