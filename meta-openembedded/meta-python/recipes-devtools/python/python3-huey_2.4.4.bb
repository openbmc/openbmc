SUMMARY = "a little task queue for python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cac039fcc82f01141cc170b48f315d4"

PYPI_PACKAGE = "huey"

SRC_URI[sha256sum] = "6a27a7862a7a982c0508ad4e548d95765e3b0d97093a51106f07540837a09c86"

RDEPENDS:${PN} += " \
	python3-datetime \
	python3-logging \
	python3-multiprocessing \
	python3-json \
"

inherit pypi setuptools3

