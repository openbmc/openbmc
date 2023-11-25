SUMMARY = "Cap'n Proto serialization/RPC system"
DESCRIPTION = "Cap’n Proto is an insanely fast data interchange format and capability-based RPC system. "
HOMEPAGE = "https://github.com/sandstorm-io/capnproto"
SECTION = "console/tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=a05663ae6cca874123bf667a60dca8c9"

SRC_URI = "git://github.com/sandstorm-io/capnproto.git;branch=release-${PV};protocol=https"
SRCREV = "c7f810a76ca88ff28613fa165927e0269255aab0"

S = "${WORKDIR}/git/c++"

inherit cmake

CXXFLAGS:append:mips = " -latomic"
CXXFLAGS:append:powerpc = " -latomic"
CXXFLAGS:append:riscv32 = " -latomic"

EXTRA_OECMAKE += "\
    -DBUILD_TESTING=OFF \
"

FILES:${PN}-compiler = "${bindir}"

PACKAGE_BEFORE_PN = "${PN}-compiler"
RDEPENDS:${PN}-dev += "${PN}-compiler"

BBCLASSEXTEND = "native nativesdk"
