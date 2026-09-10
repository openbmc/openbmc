SUMMARY = "CircuitPython data descriptor classes to represent hardware registers on I2C and SPI devices."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_Register"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec69d6e9e6c85adfb7799d7f8cf044e"

SRC_URI[sha256sum] = "ed00f49d40fc7cb1b64dc2fefed2a8ca463cc728959e70718ed7152f6591734d"

PYPI_PACKAGE = "adafruit_circuitpython_register"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "python3-core"
