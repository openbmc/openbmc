SUMMARY = "sdbus-c++ native tools"
DESCRIPTION = "Native interface code generator for development with sdbus-c++"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=1803fa9c2c3ce8cb06b4861d75310742"

inherit cmake

DEPENDS += "expat"

SRCREV = "28b78822cfc5fbec4bd9906168493e9985f586ed"
SRC_URI = "git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master;tag=v${PV};subpath=tools"

S = "${UNPACKDIR}/tools"

BBCLASSEXTEND = "native nativesdk"
