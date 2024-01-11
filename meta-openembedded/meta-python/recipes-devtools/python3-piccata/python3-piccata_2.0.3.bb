SUMMARY = "Python CoAP Toolkit"
HOMEPAGE = "https://github.com/NordicSemiconductor/piccata"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e664eb75e2791c2e505e6e1c274e6d4f"

SRCREV = "218d310e3d840715b1c8e67cefd5b6d71a2d7a1a"
SRC_URI = "git://github.com/NordicSemiconductor/piccata.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-core python3-datetime python3-io python3-logging python3-math"
