SUMMARY = "CircuitPython bus device classes to manage bus sharing."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_BusDevice"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec69d6e9e6c85adfb7799d7f8cf044e"

SRC_URI = "git://github.com/adafruit/Adafruit_CircuitPython_BusDevice.git"
SRCREV = "1bfe8005293205e2f7b2cc498ab5a946f1133b40"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS_${PN} += " \
    python3-adafruit-blinka \
    python3-core \
"
