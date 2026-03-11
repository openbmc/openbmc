SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de0a0876a688a4483bfafa764773ab39"

inherit pypi setuptools3

SRC_URI[sha256sum] = "881fb75bb1654ed55d86060bb067f2c716f8e155f57bb73fd444e53713aafec8"

RDEPENDS:${PN} += "python3-six python3-logging"
