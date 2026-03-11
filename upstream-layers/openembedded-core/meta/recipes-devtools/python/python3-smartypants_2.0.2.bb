SUMMARY = "Translator of plain ASCII punctuation characters into 'smart' typographic punctuation HTML entities"
HOMEPAGE = "https://pythonhosted.org/smartypants/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=ca325788d94ee694fef2a308c5073454"

inherit pypi setuptools3

PYPI_PACKAGE = "smartypants"
SRC_URI += "file://0001-Change-hash-bang-to-python3.patch"
SRC_URI[sha256sum] = "39d64ce1d7cc6964b698297bdf391bc12c3251b7f608e6e55d857cd7c5f800c6"

BBCLASSEXTEND = "native nativesdk"
