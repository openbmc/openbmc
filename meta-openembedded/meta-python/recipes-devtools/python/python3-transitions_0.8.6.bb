SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baac7be1f4c17620df74188e23da6d47"

inherit pypi setuptools3

SRC_URI[sha256sum] = "032e10113139852ffb3ecfa4c2a5138f87441d85c3d6ad9122bb4b0978180a8d"

RDEPENDS_${PN} += "python3-six"
