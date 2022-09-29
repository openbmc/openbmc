SUMMARY = "CircuitPython data descriptor classes to represent hardware registers on I2C and SPI devices."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_Register"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ec69d6e9e6c85adfb7799d7f8cf044e"

SRC_URI = "git://github.com/adafruit/Adafruit_CircuitPython_Register.git;branch=main;protocol=https"
SRCREV = "d1e8ac7ad9dcd65ab83749db3e5c96ffee80ebb7"
S = "${WORKDIR}/git"

DEPENDS += "python3-setuptools-scm-native"

inherit setuptools3

RDEPENDS:${PN} += "python3-core"
