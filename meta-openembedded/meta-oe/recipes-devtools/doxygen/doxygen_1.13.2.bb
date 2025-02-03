DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "bison-native flex-native"

SRC_URI = "\
    git://github.com/doxygen/doxygen.git;branch=master;protocol=https \
"

SRCREV = "26342b775ea25e6fefb53220926b20702c56fcb3"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "Release_(?P<pver>\d+(\_\d+)+)"

inherit cmake python3native

EXTRA_OECMAKE += "\
    -DFLEX_TARGET_ARG_COMPILE_FLAGS='--noline' \
    -DBISON_TARGET_ARG_COMPILE_FLAGS='--no-lines' \
"

BBCLASSEXTEND = "native nativesdk"
