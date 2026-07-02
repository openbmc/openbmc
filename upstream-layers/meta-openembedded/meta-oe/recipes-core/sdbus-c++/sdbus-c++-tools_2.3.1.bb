SUMMARY = "sdbus-c++ native tools"
DESCRIPTION = "Native interface code generator for development with sdbus-c++"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=1803fa9c2c3ce8cb06b4861d75310742"

inherit cmake

DEPENDS += "expat"

SRCREV = "ca073434d2c03c2b619f7e23f285cebbf32c7e46"
SRC_URI = "git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master;tag=v${PV};subpath=tools"

S = "${UNPACKDIR}/tools"

BBCLASSEXTEND = "native nativesdk"
