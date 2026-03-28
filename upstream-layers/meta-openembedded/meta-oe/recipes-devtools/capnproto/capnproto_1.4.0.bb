SUMMARY = "Cap'n Proto serialization/RPC system"
DESCRIPTION = "Cap’n Proto is an insanely fast data interchange format and capability-based RPC system. "
HOMEPAGE = "https://github.com/sandstorm-io/capnproto"
SECTION = "console/tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=a05663ae6cca874123bf667a60dca8c9"

SRC_URI = "git://github.com/sandstorm-io/capnproto.git;branch=release-${PV};protocol=https;tag=v${PV} \
           file://0001-Export-binaries-only-for-native-build.patch"
SRCREV = "8b892a8a11a632f5d52b877a49728808a142379a"

S = "${UNPACKDIR}/${BP}/c++"

inherit cmake

CVE_PRODUCT = "capnproto capnp"

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

CVE_STATUS[CVE-2026-32239] = "fixed-version: fixed in 1.4.0"
CVE_STATUS[CVE-2026-32240] = "fixed-version: fixed in 1.4.0"
