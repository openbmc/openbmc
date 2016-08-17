SUMMARY = "TinyXML-2 is a simple, small, efficient, C++ XML parser that can be easily integrating into other programs"
HOMEPAGE = "http://www.grinninglizard.com/tinyxml2/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://tinyxml2.cpp;endline=22;md5=c19221dbd8a66ad3090462af4c5de5e7"

SRC_URI = "git://github.com/leethomason/tinyxml2.git"

SRCREV = "1977a7258cc66fd4da7f1e9da05a4933646a7803"

S = "${WORKDIR}/git"

inherit cmake

BBCLASSEXTEND = "native"
