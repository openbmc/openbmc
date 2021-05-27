SUMMARY = "CircuitPython helper library provides higher level objects to control motors and servos."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_Motor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b72678307cc7c10910b5ef460216af07"

SRC_URI = "git://github.com/adafruit/Adafruit_CircuitPython_Motor.git"
SRCREV = "2251bfc0501d0acfb96c0a43f4f2b4c6a10ca14e"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS_${PN} += " \
    python3-adafruit-blinka \
    python3-core \
"
