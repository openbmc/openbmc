SUMMARY = "sdbus-c++ native tools"
DESCRIPTION = "Native interface code generator for development with sdbus-c++"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=1803fa9c2c3ce8cb06b4861d75310742"

inherit cmake

DEPENDS += "expat"

SRCREV = "6e8e5aadb674cccea5bdd55141db5dad887fbacd"
SRC_URI = "git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master;subpath=tools"

S = "${WORKDIR}/tools"

BBCLASSEXTEND = "native nativesdk"
