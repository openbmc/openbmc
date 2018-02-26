SUMMARY  = "Appling JSON patches in Python 2.6+ and 3.x"
HOMEPAGE = "https://github.com/stefankoegl/python-json-patch"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi setuptools

SRC_URI[md5sum] = "8ef1ceb00dcf992c9e43611f698f9279"
SRC_URI[sha256sum] = "f025c28a08ce747429ee746bb21796c3b6417ec82288f8fe6514db7398f2af8a"

RDEPENDS_${PN} += "${PYTHON_PN}-json ${PYTHON_PN}-jsonpointer ${PYTHON_PN}-netclient ${PYTHON_PN}-re ${PYTHON_PN}-stringold"

