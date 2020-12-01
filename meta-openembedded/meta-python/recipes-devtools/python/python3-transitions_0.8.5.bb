SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de0a0876a688a4483bfafa764773ab39"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e441c66a0c753d56c01c3e5e547f21dbe4a5569c939f12477475c5e81d79769b"

RDEPENDS_${PN} += "python3-six"
