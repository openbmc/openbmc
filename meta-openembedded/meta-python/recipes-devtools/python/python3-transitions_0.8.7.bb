SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baac7be1f4c17620df74188e23da6d47"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8c60ec0828cd037820726283cad5d4d77a5e31514e058b51250420e9873e9bc7"

RDEPENDS_${PN} += "python3-six"
