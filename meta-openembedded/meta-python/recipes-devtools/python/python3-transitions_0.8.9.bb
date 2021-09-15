SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baac7be1f4c17620df74188e23da6d47"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fc2ec6d6b6f986cd7e28e119eeb9ba1c9cc51ab4fbbdb7f2dedad01983fd2de0"

RDEPENDS:${PN} += "python3-six python3-logging"
