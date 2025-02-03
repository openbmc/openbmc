SUMMARY = "Translator of plain ASCII punctuation characters into 'smart' typographic punctuation HTML entities"
HOMEPAGE = "https://pythonhosted.org/smartypants/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=977036977591ac666c728921ecc54c4f"

inherit pypi setuptools3

PYPI_PACKAGE = "smartypants"
SRC_URI += "file://0001-Change-hash-bang-to-python3.patch"
SRC_URI[sha256sum] = "7812353a32022699a1aa8cd5626e01c94a946dcaeedaee2d0b382bae4c4cbf36"

BBCLASSEXTEND = "native nativesdk"
