SUMMARY = "module for creating simple ASCII tables"
HOMEPAGE = "https://github.com/foutaise/texttable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a97cdac2d9679ffdcfef3dc036d24f6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "2d2068fb55115807d3ac77a4ca68fa48803e84ebb0ee2340f858107a36522638"

BBCLASSEXTEND = "native nativesdk"
