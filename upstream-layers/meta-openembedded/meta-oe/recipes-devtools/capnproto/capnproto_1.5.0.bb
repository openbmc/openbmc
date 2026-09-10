SUMMARY = "Cap'n Proto serialization/RPC system"
DESCRIPTION = "Cap’n Proto is an insanely fast data interchange format and capability-based RPC system. "
HOMEPAGE = "https://github.com/sandstorm-io/capnproto"
SECTION = "console/tools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=a05663ae6cca874123bf667a60dca8c9"

SRC_URI = "git://github.com/sandstorm-io/capnproto.git;branch=release-${PV};protocol=https;tag=v${PV} \
           file://0001-Export-binaries-only-for-native-build.patch"
SRCREV = "373e61ec89e2359f1c362e9b2eadc552f4779306"

S = "${UNPACKDIR}/${BP}/c++"

inherit cmake

CVE_PRODUCT = "capnproto capnp"

CXXFLAGS:append:mips = " -latomic"
CXXFLAGS:append:powerpc = " -latomic"
CXXFLAGS:append:riscv32 = " -latomic"

# The build produces static archives only. Compile them position independent so
# they can be linked into shared libraries; without this any consumer that does
# so fails on the thread-local in kj/exception.c++, which non-PIC code compiles
# to the local-exec TLS model:
#
#   libkj.a(exception.c++.o): relocation R_X86_64_TPOFF32 against
#   `kj::(anonymous namespace)::threadLocalCallback' can not be used when making
#   a shared object; local-exec is incompatible with -shared
EXTRA_OECMAKE += "\
    -DBUILD_TESTING=OFF \
    -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
"

FILES:${PN}-compiler = "${bindir}"

PACKAGE_BEFORE_PN = "${PN}-compiler"
RDEPENDS:${PN}-dev += "${PN}-compiler"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2026-32239] = "fixed-version: fixed in 1.4.0"
CVE_STATUS[CVE-2026-32240] = "fixed-version: fixed in 1.4.0"
