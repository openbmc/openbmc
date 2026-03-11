SUMMARY = "Strict separation of settings from code."
HOMEPAGE = "https://github.com/henriquebastos/python-decouple/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a46375b26bb7d7603565d829a2a51782"

SRC_URI[sha256sum] = "ba6e2657d4f376ecc46f77a3a615e058d93ba5e465c01bbe57289bfb7cce680f"

PYPI_PACKAGE = "python-decouple"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-shell \
    python3-stringold \
"
