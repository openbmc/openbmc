require python-pyparted.inc

inherit distutils3

RDEPENDS_${PN} += "python3-stringold python3-codecs python3-math"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
