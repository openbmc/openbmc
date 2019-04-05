SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

PYPI_PACKAGE = "msgpack"
inherit pypi

SRC_URI[md5sum] = "be3043cc2c1e3b5ebf08463ffa71cf17"
SRC_URI[sha256sum] = "64abc6bf3a2ac301702f5760f4e6e227d0fd4d84d9014ef9a40faa9d43365259"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-io \
"
