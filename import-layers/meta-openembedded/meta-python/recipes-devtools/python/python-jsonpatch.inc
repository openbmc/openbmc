SUMMARY  = "Appling JSON patches in Python 2.6+ and 3.x"
HOMEPAGE = "https://github.com/stefankoegl/python-json-patch"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi

SRC_URI[md5sum] = "83c4c9faccb4a2ff8c1db80cbddc590d"
SRC_URI[sha256sum] = "49f29cab70e9068db3b1dc6b656cbe2ee4edf7dfe9bf5a0055f17a4b6804a4b9"

RDEPENDS_${PN} += "${PYTHON_PN}-json ${PYTHON_PN}-jsonpointer ${PYTHON_PN}-netclient ${PYTHON_PN}-stringold"

