DESCRIPTION = "Utilities to collect and visualise system statistics"
HOMEPAGE = "http://www.i-scream.org/libstatgrab/"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "ncurses"

PACKAGES_prepend = "statgrab statgrab-dbg saidar saidar-dbg ${PN}-mrtg "

# don't use the git tag unless docbooc2x is available to build the manpages
SRC_URI = "https://github.com/libstatgrab/libstatgrab/releases/download/LIBSTATGRAB_0_92/libstatgrab-0.92.tar.gz \
           file://0001-configure.ac-Do-not-use-single-line-comment.patch \
          "
SRC_URI[md5sum] = "5362b2ddbec54b3901e7d70c22cda249"
SRC_URI[sha256sum] = "5bf1906aff9ffc3eeacf32567270f4d819055d8386d98b9c8c05519012d5a196"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

# Perl5 is for tests only
EXTRA_OECONF = "--without-perl5 --with-mnttab=/proc/mounts"

# use with caution - default properties are configured to trace
PACKAGECONFIG ??= ""
PACKAGECONFIG[log4cplus] = "--with-log4cplus,--without-log4cplus,log4cplus"
PACKAGECONFIG[logging] = "--enable-logging,--disable-logging,"

inherit autotools pkgconfig

FILES_statgrab = "${bindir}/statgrab"
FILES_statgrab-dbg = "${bindir}/.debug/statgrab"
FILES_saidar = "${bindir}/saidar"
FILES_saidar-dbg = "${bindir}/.debug/saidar"
FILES_${PN}-mrtg = "${bindir}/statgrab-make-mrtg-config ${bindir}/statgrab-make-mrtg-index"
RDEPENDS_${PN}-mrtg_append = " perl statgrab"
