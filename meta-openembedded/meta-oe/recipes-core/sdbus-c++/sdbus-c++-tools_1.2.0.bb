SUMMARY = "sdbus-c++ native tools"
DESCRIPTION = "Native interface code generator for development with sdbus-c++"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=1803fa9c2c3ce8cb06b4861d75310742"

inherit cmake

DEPENDS += "expat"

SRCREV = "751c1addc4fd2f949a466f488c1b7de2ca3b76dc"
SRC_URI = "git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master;subpath=tools"

S = "${WORKDIR}/tools"

BBCLASSEXTEND = "native nativesdk"
