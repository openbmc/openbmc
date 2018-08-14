SUMMARY = "Shared library optimisation tool"
DESCRIPTION = "mklibs produces cut-down shared libraries that contain only the routines required by a particular set of executables."
HOMEPAGE = "https://launchpad.net/mklibs"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"

SRC_URI = "http://snapshot.debian.org/archive/debian/20161123T152011Z/pool/main/m/mklibs/mklibs_${PV}.tar.xz \
	file://ac_init_fix.patch\
	file://fix_STT_GNU_IFUNC.patch\
	file://sysrooted-ldso.patch \
	file://avoid-failure-on-symbol-provided-by-application.patch \
	file://show-GNU-unique-symbols-as-provided-symbols.patch \
	file://fix_cross_compile.patch \
"

SRC_URI[md5sum] = "39b08a173454e5210ab3f598e94179bf"
SRC_URI[sha256sum] = "6f0cf24ade13fff76e943c003413d85c3e497c984c95c1ecea1c9731ca86f13c"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/m/mklibs/"

inherit autotools gettext native

S = "${WORKDIR}/mklibs"
