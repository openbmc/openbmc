DESCRIPTION = "Powerful Python library for atomic file writes"
HOMEPAGE = "https://github.com/untitaker/python-atomicwrites"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91cc36cfafeefb7863673bcfcb1d4da4"

SRC_URI[md5sum] = "ce11f780a4ce0fce8a55d64494a88178"
SRC_URI[sha256sum] = "75a9445bac02d8d058d5e1fe689654ba5a6556a1dfd8ce6ec55a0ed79866cfa6"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-misc"
