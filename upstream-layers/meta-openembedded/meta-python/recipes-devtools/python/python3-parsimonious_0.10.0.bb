SUMMARY = "Parsimonious aims to be the fastest arbitrary-lookahead parser written in pure Python."
HOMEPAGE = "https://github.com/erikrose/parsimonious"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "8281600da180ec8ae35427a4ab4f7b82bfec1e3d1e52f80cb60ea82b9512501c"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-regex"
