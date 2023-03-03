SUMMARY = "a little task queue for python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cac039fcc82f01141cc170b48f315d4"

PYPI_PACKAGE = "huey"

SRC_URI[sha256sum] = "760cf150deff1fa34b852da37701a5a750d1148f03ea07aa2b3764dc6060b4c3"

RDEPENDS:${PN} += " \
	python3-datetime \
	python3-logging \
	python3-multiprocessing \
	python3-json \
"

inherit pypi setuptools3

