SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de0a0876a688a4483bfafa764773ab39"

inherit pypi setuptools3

SRC_URI[sha256sum] = "3542c37108e93e2ae5f215208ec5732c94a772937854a102cd7345b967fee61b"

RDEPENDS:${PN} += "python3-six python3-logging"
