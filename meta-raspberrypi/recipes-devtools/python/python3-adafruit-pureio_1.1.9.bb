SUMMARY = "Pure python (i.e. no native extensions) access to Linux IO    including I2C and SPI. Drop in replacement for smbus and spidev modules."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Python_PureIO"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a21fcca821a506d4c36f7bbecc0d009"

SRC_URI = "git://github.com/adafruit/Adafruit_Python_PureIO.git;branch=main;protocol=https"
SRCREV = "383b615ce9ff5bbefdf77652799f380016fda353"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-core \
    python3-ctypes \
    python3-fcntl \
"
