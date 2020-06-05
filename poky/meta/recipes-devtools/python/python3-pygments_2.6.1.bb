SUMMARY = "Pygments is a syntax highlighting package written in Python."
DESCRIPTION = "Pygments is a syntax highlighting package written in Python."
HOMEPAGE = "http://pygments.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1d7b7bffbfeaa14083fd2bd3236aea8"

inherit setuptools3
SRC_URI[md5sum] = "a48c5219de92f12c41acba814730b31a"
SRC_URI[sha256sum] = "647344a061c249a3b74e230c739f434d7ea4d8b1d5f3721bc0f3558049b38f44"

DEPENDS += "\
            ${PYTHON_PN} \
            "

PYPI_PACKAGE = "Pygments"

inherit pypi

BBCLASSEXTEND = "native nativesdk"

