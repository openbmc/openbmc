SUMMARY = "Shared library optimisation tool"
DESCRIPTION = "mklibs produces cut-down shared libraries that contain only the routines required by a particular set of executables."
HOMEPAGE = "https://launchpad.net/mklibs"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"

SRC_URI = "http://snapshot.debian.org/archive/debian/20180828T214102Z/pool/main/m/mklibs/mklibs_${PV}.tar.xz \
	file://ac_init_fix.patch\
	file://fix_STT_GNU_IFUNC.patch\
	file://sysrooted-ldso.patch \
	file://avoid-failure-on-symbol-provided-by-application.patch \
	file://show-GNU-unique-symbols-as-provided-symbols.patch \
	file://fix_cross_compile.patch \
"

SRC_URI[md5sum] = "6b6eeb9b4016c6a7317acc28c89e32cc"
SRC_URI[sha256sum] = "3af0b6bd35e5b6fc58d8b68827fbae2ff6b7e20dd2b238ccb9b49d84722066c2"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/m/mklibs/"

inherit autotools gettext native
