SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=98419e351433ac106a24e3ad435930bc"

inherit setuptools3
SRC_URI[sha256sum] = "f398865f7eb6874156579fdf36bc840a03cab64d1cde9e93d68f46a425ec52c6"

DEPENDS += "\
            ${PYTHON_PN} \
            "

PYPI_PACKAGE = "Pygments"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

