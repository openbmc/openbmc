SUMMARY = "Hamcrest framework for matcher objects"
HOMEPAGE = "https://github.com/hamcrest/PyHamcrest"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=79391bf1501c898472d043f36e960612"

PYPI_PACKAGE = "PyHamcrest"

SRC_URI[md5sum] = "7a086f0b067f8d38958ec32f054559b4"
SRC_URI[sha256sum] = "412e00137858f04bde0729913874a48485665f2d36fe9ee449f26be864af9316"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-six"
