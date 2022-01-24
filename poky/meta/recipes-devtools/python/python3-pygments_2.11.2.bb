SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=98419e351433ac106a24e3ad435930bc"

inherit setuptools3
SRC_URI[sha256sum] = "4e426f72023d88d03b2fa258de560726ce890ff3b630f88c21cbb8b2503b8c6a"

DEPENDS += "\
            ${PYTHON_PN} \
            "

PYPI_PACKAGE = "Pygments"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

