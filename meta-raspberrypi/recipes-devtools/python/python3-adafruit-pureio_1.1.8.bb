SUMMARY = "Pure python (i.e. no native extensions) access to Linux IO    including I2C and SPI. Drop in replacement for smbus and spidev modules."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Python_PureIO"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a21fcca821a506d4c36f7bbecc0d009"

SRC_URI = "git://github.com/adafruit/Adafruit_Python_PureIO.git;branch=main"
SRCREV = "f4d0973da05b8b21905ff6bab69cdb652128f342"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS_${PN} += " \
    python3-core \
    python3-ctypes \
    python3-fcntl \
"
