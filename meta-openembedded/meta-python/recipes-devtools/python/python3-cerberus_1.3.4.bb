SUMMARY = "Lightweight, extensible schema and data validation tool for Python dictionaries."
HOMEPAGE = "http://docs.python-cerberus.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48f8e9432d0dac5e0e7a18211a0bacdb"

RDEPENDS:${PN} += "python3-setuptools"

# The PyPI package uses a capital letter so we have to specify this explicitly
PYPI_PACKAGE = "Cerberus"
inherit pypi setuptools3

SRC_URI[sha256sum] = "d1b21b3954b2498d9a79edf16b3170a3ac1021df88d197dc2ce5928ba519237c"
