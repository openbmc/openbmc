SUMMARY = "Device Tree Compiler"
HOMEPAGE = "https://devicetree.org/"
DESCRIPTION = "The Device Tree Compiler is a tool used to manipulate the Open-Firmware-like device tree used by PowerPC kernels."
SECTION = "bootloader"
LICENSE = "GPLv2 | BSD"
DEPENDS = "flex-native bison-native"

SRC_URI = "git://git.kernel.org/pub/scm/utils/dtc/dtc.git \
           file://make_install.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

EXTRA_OEMAKE='NO_PYTHON=1 PREFIX="${prefix}" LIBDIR="${libdir}" DESTDIR="${D}"'

inherit pkgconfig

S = "${WORKDIR}/git"

do_install () {
	oe_runmake install
}

PACKAGES =+ "${PN}-misc"
FILES_${PN}-misc = "${bindir}/convert-dtsv0 ${bindir}/ftdump ${bindir}/dtdiff"

RDEPENDS_${PN}-misc += "bash diffutils"
