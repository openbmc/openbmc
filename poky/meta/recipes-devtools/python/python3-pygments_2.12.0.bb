SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=36a13c90514e2899f1eba7f41c3ee592"

inherit setuptools3
SRC_URI[sha256sum] = "5eb116118f9612ff1ee89ac96437bb6b49e8f04d8a13b514ba26f620208e26eb"

DEPENDS += "\
            ${PYTHON_PN} \
            "

PYPI_PACKAGE = "Pygments"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

