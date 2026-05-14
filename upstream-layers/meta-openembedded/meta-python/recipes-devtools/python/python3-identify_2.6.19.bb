SUMMARY = "File identification library for Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bbdc006359f3157660173ec7f133a80e"

PYPI_PACKAGE = "identify"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6be5020c38fcb07da56c53733538a3081ea5aa70d36a156f83044bfbf9173842"

RDEPENDS:${PN} = " \
	python3-ukkonen \
"
