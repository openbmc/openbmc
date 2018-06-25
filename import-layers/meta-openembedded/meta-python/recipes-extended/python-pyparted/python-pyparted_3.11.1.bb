require python-pyparted.inc

PV = "3.10.7+git${SRCPV}"

inherit distutils

RDEPENDS_${PN} += "python-stringold python-codecs python-math python-subprocess"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
