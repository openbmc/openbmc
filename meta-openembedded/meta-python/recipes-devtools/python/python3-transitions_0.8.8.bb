SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baac7be1f4c17620df74188e23da6d47"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e7a86b31a161a76133f189b3ae9dad2755a80ea4c1e0eee1805648d021fb677d"

RDEPENDS_${PN} += "python3-six"
