DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "https://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI += "file://d19ad6cc086684ee74db250f5fd35227c98e678a.patch"
SRC_URI[sha256sum] = "aee74de74ac6634a1dac1ed58dc93b5dc2abaef3c7b5e76fd7f195f1662f25ef"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
	python3-dnspython \
	python3-six \
	python3-greenlet \
"
