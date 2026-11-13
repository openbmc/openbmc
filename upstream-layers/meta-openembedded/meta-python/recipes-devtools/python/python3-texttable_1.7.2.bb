SUMMARY = "module for creating simple ASCII tables"
HOMEPAGE = "https://github.com/foutaise/texttable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a97cdac2d9679ffdcfef3dc036d24f6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "dc16ea6b35fbf5291ba6ef79d79e37d9b3ca6acd7bae3ca3450488d7c2e19c99"

BBCLASSEXTEND = "native nativesdk"
