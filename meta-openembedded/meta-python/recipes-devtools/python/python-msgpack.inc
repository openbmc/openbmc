SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

PYPI_PACKAGE = "msgpack"
inherit pypi

SRC_URI[md5sum] = "1b96537be6f5186fed0d131aaa59ef26"
SRC_URI[sha256sum] = "4008c72f5ef2b7936447dcb83db41d97e9791c83221be13d5e19db0796df1972"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-io \
"
