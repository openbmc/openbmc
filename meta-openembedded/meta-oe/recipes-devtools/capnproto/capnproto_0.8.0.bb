SUMMARY = "Cap'n Proto serialization/RPC system"
DESCRIPTION = "Cap’n Proto is an insanely fast data interchange format and capability-based RPC system. "
HOMEPAGE = "https://github.com/sandstorm-io/capnproto"
SECTION = "console/tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=a05663ae6cca874123bf667a60dca8c9"

SRC_URI = "git://github.com/sandstorm-io/capnproto.git;branch=release-${PV} \
           file://0001-mutex-Fix-build-on-32-bit-architectures-using-64-bit.patch;patchdir=../ \
           "
SRCREV = "57a4ca5af5a7f55b768a9d9d6655250bffb1257f"

S = "${WORKDIR}/git/c++"

inherit cmake

EXTRA_OECMAKE += "\
    -DBUILD_TESTING=OFF \
"

FILES_${PN}-compiler = "${bindir}"

PACKAGE_BEFORE_PN = "${PN}-compiler"
RDEPENDS_${PN}-dev += "${PN}-compiler"

BBCLASSEXTEND = "native nativesdk"
