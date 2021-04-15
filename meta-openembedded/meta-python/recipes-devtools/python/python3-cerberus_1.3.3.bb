SUMMARY = "Lightweight, extensible schema and data validation tool for Python dictionaries."
HOMEPAGE = "http://docs.python-cerberus.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48f8e9432d0dac5e0e7a18211a0bacdb"

# The PyPI package uses a capital letter so we have to specify this explicitly
PYPI_PACKAGE = "Cerberus"
inherit pypi setuptools3

SRC_URI[sha256sum] = "eec10585c33044fb7c69650bc5b68018dac0443753337e2b07684ee0f3c83329"
