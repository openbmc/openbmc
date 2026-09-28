SUMMARY = "Pure python (i.e. no native extensions) access to Linux IO    including I2C and SPI. Drop in replacement for smbus and spidev modules."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Python_PureIO"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a21fcca821a506d4c36f7bbecc0d009"

SRC_URI[sha256sum] = "c4cfbb365731942d1f1092a116f47dfdae0aef18c5b27f1072b5824ad5ea8c7c"

PYPI_PACKAGE = "Adafruit_PureIO"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-core \
    python3-ctypes \
    python3-fcntl \
"

do_install_ptest:append() {
    if [ "${ENABLE_I2C}" != "1" ]; then
        bbwarn "The tests require I2C. Set ENABLE_I2C to \"1\" to enable it."
    fi
}
