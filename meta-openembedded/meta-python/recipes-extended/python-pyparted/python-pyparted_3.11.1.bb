require python-pyparted.inc

inherit distutils

RDEPENDS_${PN} += "python-stringold python-codecs python-math python-subprocess"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
