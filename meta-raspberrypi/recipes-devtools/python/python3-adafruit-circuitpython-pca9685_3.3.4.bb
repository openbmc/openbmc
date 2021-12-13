SUMMARY = "CircuitPython driver for motor, stepper, and servo based on PCA9685."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_PCA9685"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7eb6b599fb0cfb06485c64cd4242f62"

SRC_URI = "git://github.com/adafruit/Adafruit_CircuitPython_PCA9685.git;branch=main;protocol=https"
SRCREV = "2780c4102f4c23fbab252aa1198b61ba7e2d1b2c"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-adafruit-blinka \
    python3-adafruit-circuitpython-busdevice \
    python3-adafruit-circuitpython-register \
    python3-core \
"
