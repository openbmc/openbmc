DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "https://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI += "file://d19ad6cc086684ee74db250f5fd35227c98e678a.patch"
SRC_URI[sha256sum] = "66d1883f276e94d040d007e99d7085e6aa5f2738c5b8783d165a137050dc8fb6"

CVE_PRODUCT = "eventlet"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
	python3-dnspython \
	python3-greenlet \
"

CVE_STATUS[CVE-2023-29483] = "fixed-version: fixed since v0.35.2"
