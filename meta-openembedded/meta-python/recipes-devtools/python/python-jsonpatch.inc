SUMMARY  = "Appling JSON patches in Python 2.6+ and 3.x"
HOMEPAGE = "https://github.com/stefankoegl/python-json-patch"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi

SRC_URI[md5sum] = "e86503f05fa192fa870d7004b8ce929a"
SRC_URI[sha256sum] = "cbb72f8bf35260628aea6b508a107245f757d1ec839a19c34349985e2c05645a"

RDEPENDS_${PN} += "${PYTHON_PN}-json ${PYTHON_PN}-jsonpointer ${PYTHON_PN}-netclient ${PYTHON_PN}-stringold"

