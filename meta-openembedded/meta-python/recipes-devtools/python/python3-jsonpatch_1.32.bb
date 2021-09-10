SUMMARY  = "Appling JSON patches in Python 2.6+ and 3.x"
HOMEPAGE = "https://github.com/stefankoegl/python-json-patch"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi setuptools3

SRC_URI[sha256sum] = "b6ddfe6c3db30d81a96aaeceb6baf916094ffa23d7dd5fa2c13e13f8b6e600c2"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-jsonpointer \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-stringold \
"
