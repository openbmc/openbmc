SUMMARY = "Execute commands and copy files over SSH to multiple machines at once."
HOMEPAGE = "https://github.com/krig/parallax/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=52c67ffa6102f288a0347f8c5802fd18"

SRC_URI[md5sum] = "f8d04e9e246291536b705df7f60a55c9"
SRC_URI[sha256sum] = "e9e4dc500f1306a638df0f19d4ef976a623b260fc614c55cb1fd0d8410b3c4ba"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-fcntl ${PYTHON_PN}-threading ${PYTHON_PN}-unixadmin"

BBCLASSEXTEND = "native nativesdk"
