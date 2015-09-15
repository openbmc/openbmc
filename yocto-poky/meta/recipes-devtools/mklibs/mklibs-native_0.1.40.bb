SUMMARY = "Shared library optimisation tool"
DESCRIPTION = "mklibs produces cut-down shared libraries that contain only the routines required by a particular set of executables."
HOMEPAGE = "https://launchpad.net/mklibs"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"
DEPENDS = "python-native"

SRC_URI = "http://ftp.de.debian.org/debian/pool/main/m/mklibs/${BPN}_${PV}.tar.xz \
	file://ac_init_fix.patch\
	file://fix_STT_GNU_IFUNC.patch\
	file://sysrooted-ldso.patch \
	file://avoid-failure-on-symbol-provided-by-application.patch \
	file://show-GNU-unique-symbols-as-provided-symbols.patch \
	file://fix_cross_compile.patch \
"

SRC_URI[md5sum] = "e1dafe5f962caa9dc5f2651c0723812a"
SRC_URI[sha256sum] = "1db24ae779d21ccfed49f22e49a2b7ee43ec0e9197bc206d81cd02f96e91e125"

inherit autotools gettext native pythonnative
