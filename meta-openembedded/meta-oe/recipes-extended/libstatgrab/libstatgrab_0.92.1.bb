DESCRIPTION = "Utilities to collect and visualise system statistics"
HOMEPAGE = "http://www.i-scream.org/libstatgrab/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "ncurses"

PACKAGES:prepend = "statgrab statgrab-dbg saidar saidar-dbg ${PN}-mrtg "

# don't use the git tag unless docbooc2x is available to build the manpages
SRC_URI = "https://github.com/libstatgrab/libstatgrab/releases/download/LIBSTATGRAB_0_92_1/libstatgrab-${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "5688aa4a685547d7174a8a373ea9d8ee927e766e3cc302bdee34523c2c5d6c11"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

# Perl5 is for tests only
EXTRA_OECONF = "--without-perl5 --with-mnttab=/proc/mounts"

# use with caution - default properties are configured to trace
PACKAGECONFIG ??= ""
PACKAGECONFIG[log4cplus] = "--with-log4cplus,--without-log4cplus,log4cplus"
PACKAGECONFIG[logging] = "--enable-logging,--disable-logging,"

inherit autotools pkgconfig

FILES:statgrab = "${bindir}/statgrab"
FILES:statgrab-dbg = "${bindir}/.debug/statgrab"
FILES:saidar = "${bindir}/saidar"
FILES:saidar-dbg = "${bindir}/.debug/saidar"
FILES:${PN}-mrtg = "${bindir}/statgrab-make-mrtg-config ${bindir}/statgrab-make-mrtg-index"
RDEPENDS:${PN}-mrtg:append = " perl statgrab"
