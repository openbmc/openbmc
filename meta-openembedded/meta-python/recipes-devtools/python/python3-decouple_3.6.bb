SUMMARY = "Strict separation of settings from code."
HOMEPAGE = "https://github.com/henriquebastos/python-decouple/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a46375b26bb7d7603565d829a2a51782"

SRC_URI[sha256sum] = "2838cdf77a5cf127d7e8b339ce14c25bceb3af3e674e039d4901ba16359968c7"

PYPI_PACKAGE = "python-decouple"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-shell \
    python3-stringold \
"
