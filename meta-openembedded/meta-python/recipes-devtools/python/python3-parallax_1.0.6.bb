SUMMARY = "Execute commands and copy files over SSH to multiple machines at once."
HOMEPAGE = "https://github.com/krig/parallax/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=52c67ffa6102f288a0347f8c5802fd18"

SRC_URI[md5sum] = "e312397b083426af84db7076dc2a28d7"
SRC_URI[sha256sum] = "c16703202ff67aed4740c0727df304abe9f3e7851e653533b24de21b338d9081"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-fcntl ${PYTHON_PN}-threading ${PYTHON_PN}-unixadmin"

BBCLASSEXTEND = "native nativesdk"
