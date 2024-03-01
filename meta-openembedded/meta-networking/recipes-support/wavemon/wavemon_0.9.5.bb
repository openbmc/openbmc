SUMMARY = "wavemon is a wireless device monitoring application"
HOMEPAGE = "https://github.com/uoaerg/wavemon"
DESCRIPTION = "wavemon is a wireless device monitoring application that \
	allows you to watch signal and noise levels, packet \
	statistics, device configuration and network parameters of \
	your wireless network hardware."

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

PACKAGECONFIG ??= "cap"
PACKAGECONFIG[cap] = "--with-libcap,--without-libcap,libcap"

DEPENDS = "libnl ncurses"

SRC_URI = "git://github.com/uoaerg/wavemon;branch=master;protocol=https"
SRCREV = "a7c2f190e010db508ead31fa952478e2a1c13d64"

# Needs some help to find libnl3 headers.
# Reorder -pthread flag on the command line.
EXTRA_OEMAKE = "\
    CC='${CC}' CFLAGS='${CFLAGS} -pthread -I${STAGING_INCDIR}/libnl3' \
"

S = "${WORKDIR}/git"

# wavemon does not support using out-of-tree builds
inherit autotools-brokensep pkgconfig
