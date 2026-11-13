SUMMARY = "Python module to handle standardized numbers and codes"
HOMEPAGE = "https://arthurdejong.org/python-stdnum/"
LICENSE = "LGPL-2.0-or-later AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

SRC_URI[sha256sum] = "e95fcfa858a703d4a40130cb3eaac133c60d8808a7f3c98efeedac968c2479b9"

PYPI_PACKAGE = "python-stdnum"

inherit pypi setuptools3

PACKAGECONFIG[soap] = ",,,python3-zeep"

RDEPENDS:${PN} += " \
    python3-core \
    python3-crypt \
    python3-datetime \
    python3-io \
    python3-json \
    python3-lxml \
    python3-numbers \
    python3-pydoc \
    python3-requests \
"
