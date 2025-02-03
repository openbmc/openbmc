SUMMARY = "Lightweight, extensible schema and data validation tool for Python dictionaries."
HOMEPAGE = "http://docs.python-cerberus.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48f8e9432d0dac5e0e7a18211a0bacdb"

UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_poetry_core

SRC_URI[sha256sum] = "ecf249665400a0b7a9d5e4ee1ffc234fd5d003186d3e1904f70bc14038642c13"
