SUMMARY = "Lightweight, extensible schema and data validation tool for Python dictionaries."
HOMEPAGE = "http://docs.python-cerberus.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48f8e9432d0dac5e0e7a18211a0bacdb"

# The PyPI package uses a capital letter so we have to specify this explicitly
PYPI_PACKAGE = "Cerberus"
inherit pypi python_poetry_core

SRC_URI[sha256sum] = "81011e10266ef71b6ec6d50e60171258a5b134d69f8fb387d16e4936d0d47642"
