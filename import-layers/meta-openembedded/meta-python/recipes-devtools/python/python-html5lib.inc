SUMMARY = "HTML parser based on the WHATWG HTML specifcation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ba5ada9e6fead1fdc32f43c9f10ba7c"

SRC_URI[md5sum] = "8578e4e3a341436cb9743a9e4a299239"
SRC_URI[sha256sum] = "ee747c0ffd3028d2722061936b5c65ee4fe13c8e4613519b4447123fc4546298"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-webencodings \
    ${PYTHON_PN}-xml \
    "
