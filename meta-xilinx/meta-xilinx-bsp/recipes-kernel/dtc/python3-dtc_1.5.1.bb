SUMMARY = "Device Tree Compiler"
HOMEPAGE = "https://devicetree.org/"
DESCRIPTION = "The Device Tree Compiler is a tool used to manipulate the Open-Firmware-like device tree used by PowerPC kernels."
SECTION = "bootloader"
LICENSE = "GPLv2 | BSD"
DEPENDS = "flex-native bison-native swig-native"

SRC_URI = "git://git.kernel.org/pub/scm/utils/dtc/dtc.git"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

LIC_FILES_CHKSUM = "file://libfdt.i;beginline=1;endline=6;md5=afda088c974174a29108c8d80b5dce90"

SRCREV = "60e0db3d65a1218b0d5a29474e769f28a18e3ca6"

S = "${WORKDIR}/git/pylibfdt"

DEPENDS += "libyaml dtc"

inherit distutils3

do_configure_prepend() {
    (cd ${S}/../ ; make version_gen.h )
}

BBCLASSEXTEND = "native nativesdk"

