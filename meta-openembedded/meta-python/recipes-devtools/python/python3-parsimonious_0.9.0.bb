SUMMARY = "Parsimonious aims to be the fastest arbitrary-lookahead parser written in pure Python."
HOMEPAGE = "https://github.com/erikrose/parsimonious"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "b2ad1ae63a2f65bd78f5e0a8ac510a98f3607a43f1db2a8d46636a5d9e4a30c1"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-regex"
