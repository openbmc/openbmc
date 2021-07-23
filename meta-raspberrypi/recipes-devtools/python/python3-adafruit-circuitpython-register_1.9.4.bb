SUMMARY = "CircuitPython data descriptor classes to represent hardware registers on I2C and SPI devices."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_Register"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec69d6e9e6c85adfb7799d7f8cf044e"

SRC_URI = "git://github.com/adafruit/Adafruit_CircuitPython_Register.git;branch=main"

S = "${WORKDIR}/git"
SRCREV = "5fee6e0c3878110844bc51e16063eeae7d94c457"

DEPENDS += "python3-setuptools-scm-native"

inherit setuptools3

RDEPENDS_${PN} += "python3-core"
