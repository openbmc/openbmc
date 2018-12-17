SUMMARY = "Cap'n Proto serialization/RPC system"
DESCRIPTION = "Cap’n Proto is an insanely fast data interchange format and capability-based RPC system. "
HOMEPAGE = "https://github.com/sandstorm-io/capnproto"
SECTION = "console/tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=0a5b5b742baf10cc1c158579eba7fb1d"

SRC_URI = "git://github.com/sandstorm-io/capnproto.git;branch=release-${PV}"
SRCREV = "c949a18da5f041a36cc218c5c4b79c7705999b4f"

S = "${WORKDIR}/git/c++"

inherit cmake

EXTRA_OECMAKE += "\
    -DBUILD_TESTING=OFF \
"

FILES_${PN}-compiler = "${bindir}"

PACKAGE_BEFORE_PN = "${PN}-compiler"
RDEPENDS_${PN}-dev += "${PN}-compiler"

BBCLASSEXTEND = "native nativesdk"
