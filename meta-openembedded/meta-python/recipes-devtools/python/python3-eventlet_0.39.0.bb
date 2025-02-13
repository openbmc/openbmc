DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI += "file://d19ad6cc086684ee74db250f5fd35227c98e678a.patch"
SRC_URI[sha256sum] = "01ba0aa6ee2452690fc02b274a2409598a13b997c7b5af9dc66600fa42015a79"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
	python3-dnspython \
	python3-six \
	python3-greenlet \
"
