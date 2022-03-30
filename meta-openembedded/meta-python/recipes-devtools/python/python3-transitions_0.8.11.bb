SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baac7be1f4c17620df74188e23da6d47"

inherit pypi setuptools3

SRC_URI[sha256sum] = "7b20d32906ea4d60ee6f6c1f5dc9c9f178802425c5b155213eb0f25c277f04e4"

RDEPENDS:${PN} += "python3-six python3-logging"
