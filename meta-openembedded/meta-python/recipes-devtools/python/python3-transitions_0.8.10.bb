SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baac7be1f4c17620df74188e23da6d47"

inherit pypi setuptools3

SRC_URI[sha256sum] = "b0385975a842e885c1a55c719d2f90164471665794d39d51f9eb3f11e1d9c8ac"

RDEPENDS:${PN} += "python3-six python3-logging"
