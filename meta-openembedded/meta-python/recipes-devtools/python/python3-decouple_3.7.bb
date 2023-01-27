SUMMARY = "Strict separation of settings from code."
HOMEPAGE = "https://github.com/henriquebastos/python-decouple/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a46375b26bb7d7603565d829a2a51782"

SRC_URI[sha256sum] = "e88a8d6bdf3b07d471a854099e455e20a6fa7a4d6ecf8631b250e3db654336e6"

PYPI_PACKAGE = "python-decouple"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-shell \
    python3-stringold \
"
