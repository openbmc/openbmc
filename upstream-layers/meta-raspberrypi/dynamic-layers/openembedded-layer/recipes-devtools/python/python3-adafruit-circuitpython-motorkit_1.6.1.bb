SUMMARY = "CircuitPython helper library for DC & Stepper Motor FeatherWing, Shield, and Pi Hat kits."
HOMEPAGE = "https://github.com/adafruit/Adafruit_CircuitPython_MotorKit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ad4a8854b39ad474755ef1aea813bac"

SRC_URI = "git://github.com/adafruit/Adafruit_CircuitPython_MotorKit.git;branch=main;protocol=https"
SRCREV = "8c1462b4129b21f6db156d1517abb017bb74b982"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-adafruit-blinka \
    python3-adafruit-circuitpython-busdevice \
    python3-adafruit-circuitpython-motor \
    python3-adafruit-circuitpython-pca9685 \
    python3-adafruit-circuitpython-register \
    python3-core \
"
COMPATIBLE_HOST:libc-musl:class-target = "null"
