SUMMARY = "Software watchdog"
DESCRIPTION = "Watchdog is a daemon that checks if your system is still \
working. If programs in user space are not longer executed \
it will reboot the system."
HOMEPAGE = "http://watchdog.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=172030&atid=860194"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecc0551bf54ad97f6b541720f84d6569"

SRC_URI = "${SOURCEFORGE_MIRROR}/watchdog/watchdog-${PV}.tar.gz \
           file://fixsepbuild.patch \
	   file://fix-ping-failure.patch \
	   file://watchdog-init.patch \
	   file://watchdog-conf.patch \
	   file://0001-Fix-build-issues-found-with-non-glibc-C-libraries.patch \
"

SRC_URI[md5sum] = "5b2dba0c593942f4acc100bca0d560c4"
SRC_URI[sha256sum] = "620b2f49e9879f2e85c73d4c1f422f9101e6b38e824fea2414befd8bb6866ad1"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/watchdog/files/watchdog/"
UPSTREAM_CHECK_REGEX = "/watchdog/(?P<pver>(\d+[\.\-_]*)+)/"

inherit autotools
inherit update-rc.d

DEPENDS_append_libc-musl = " libtirpc "
CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc "
LDFLAGS_append_libc-musl = " -ltirpc "
EXTRA_OECONF_append_libc-musl = " --disable-nfs "

INITSCRIPT_NAME = "watchdog.sh"
INITSCRIPT_PARAMS = "start 15 1 2 3 4 5 . stop 85 0 6 ."

RRECOMMENDS_${PN} = "kernel-module-softdog"

do_install_append() {
	install -D ${S}/redhat/watchdog.init ${D}/${sysconfdir}/init.d/watchdog.sh
}
