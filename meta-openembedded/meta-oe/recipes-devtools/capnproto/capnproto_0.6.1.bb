SUMMARY = "Cap'n Proto serialization/RPC system"
DESCRIPTION = "Cap’n Proto is an insanely fast data interchange format and capability-based RPC system. "
HOMEPAGE = "https://github.com/sandstorm-io/capnproto"
SECTION = "console/tools"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=0a5b5b742baf10cc1c158579eba7fb1d"

SRCREV = "c949a18da5f041a36cc218c5c4b79c7705999b4f"
SRC_URI = "git://github.com/sandstorm-io/capnproto.git;branch=release-${PV}"

EXTRA_OECMAKE += "\
    -DBUILD_TESTING=OFF \
"

inherit cmake

S = "${WORKDIR}/git/c++"

PACKAGE_BEFORE_PN = "${PN}-compiler"
RDEPENDS_${PN}-dev += "${PN}-compiler"

FILES_${PN}-dev += "${libdir}/cmake"
FILES_${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
