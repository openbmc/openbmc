SUMMARY = "Shared library optimisation tool"
DESCRIPTION = "mklibs produces cut-down shared libraries that contain only the routines required by a particular set of executables."
HOMEPAGE = "https://launchpad.net/mklibs"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"
DEPENDS = "python-native"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160207T221625Z/pool/main/m/${BPN}/${BPN}_${PV}.tar.xz \
	file://ac_init_fix.patch\
	file://fix_STT_GNU_IFUNC.patch\
	file://sysrooted-ldso.patch \
	file://avoid-failure-on-symbol-provided-by-application.patch \
	file://show-GNU-unique-symbols-as-provided-symbols.patch \
	file://fix_cross_compile.patch \
"

SRC_URI[md5sum] = "6b2979876a611717df3d49e7f9cf291d"
SRC_URI[sha256sum] = "058c7349f8ec8a03b529c546a95cd6426741bd819f1e1211f499273eb4bf5d89"

inherit autotools gettext native pythonnative
