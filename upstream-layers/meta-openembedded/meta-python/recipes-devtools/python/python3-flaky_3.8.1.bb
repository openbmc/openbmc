DESCRIPTION = "Plugin for pytest that automatically reruns flaky tests."
HOMEPAGE = "https://github.com/box/flaky"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=55c52d858ecd501c970ad04d2c70ebc6"

SRC_URI[sha256sum] = "47204a81ec905f3d5acfbd61daeabcada8f9d4031616d9bcb0618461729699f5"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-pytest \
"
