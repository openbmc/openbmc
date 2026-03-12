SUMMARY = "Lightweight, extensible schema and data validation tool for Python dictionaries."
HOMEPAGE = "https://docs.python-cerberus.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48f8e9432d0dac5e0e7a18211a0bacdb"

UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_poetry_core

SRC_URI[sha256sum] = "579554887ffd189226774b87570f4a76db75cf0efcbaffcacd5e98b8ee877f61"
