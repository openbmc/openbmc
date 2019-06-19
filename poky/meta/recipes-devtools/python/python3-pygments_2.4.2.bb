SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1d7b7bffbfeaa14083fd2bd3236aea8"

inherit setuptools3
SRC_URI[md5sum] = "5ecc3fbb2a783e917b369271fc0e6cd1"
SRC_URI[sha256sum] = "881c4c157e45f30af185c1ffe8d549d48ac9127433f2c380c24b84572ad66297"

DEPENDS += "\
            ${PYTHON_PN} \
            "

PYPI_PACKAGE = "Pygments"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

