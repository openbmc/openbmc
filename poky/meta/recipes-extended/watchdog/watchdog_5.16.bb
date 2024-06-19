SUMMARY = "Software watchdog"
DESCRIPTION = "Watchdog is a daemon that checks if your system is still \
working. If programs in user space are not longer executed \
it will reboot the system."
HOMEPAGE = "http://watchdog.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=172030&atid=860194"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=084236108b1d4a9851bf5213fea586fd"

SRC_URI = "${SOURCEFORGE_MIRROR}/watchdog/watchdog-${PV}.tar.gz \
           file://0001-watchdog-remove-interdependencies-of-watchdog-and-wd.patch \
           file://watchdog.init \
           file://wd_keepalive.init \
           file://0001-wd_keepalive.service-use-run-instead-of-var-run.patch \
           file://0001-shutdown-Do-not-guard-sys-quota.h-sys-swap.h-and-sys.patch \
"

SRC_URI[md5sum] = "1b4f51cabc64d1bee2fce7cdd626831f"
SRC_URI[sha256sum] = "b8e7c070e1b72aee2663bdc13b5cc39f76c9232669cfbb1ac0adc7275a3b019d"

# Can be dropped when the output next changes, avoids failures after
# reproducibility issues

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/watchdog/files/watchdog/"
UPSTREAM_CHECK_REGEX = "/watchdog/(?P<pver>(\d+[\.\-_]*)+)/"

inherit autotools update-rc.d systemd pkgconfig

EXTRA_OECONF += " --disable-nfs "
CACHED_CONFIGUREVARS += "ac_cv_path_PATH_SENDMAIL=${sbindir}/sendmail"

INITSCRIPT_PACKAGES = "${PN} ${PN}-keepalive"

INITSCRIPT_NAME:${PN} = "watchdog"
INITSCRIPT_PARAMS:${PN} = "start 25 1 2 3 4 5 . stop 85 0 6 ."

INITSCRIPT_NAME:${PN}-keepalive = "wd_keepalive"
INITSCRIPT_PARAMS:${PN}-keepalive = "start 25 1 2 3 4 5 . stop 85 0 6 ."

SYSTEMD_PACKAGES = "${PN} ${PN}-keepalive"
SYSTEMD_SERVICE:${PN} = "watchdog.service"
SYSTEMD_SERVICE:${PN}-keepalive = "wd_keepalive.service"
# When using systemd, consider making use of internal watchdog support of systemd.
# See RuntimeWatchdogSec in /etc/systemd/system.conf.
SYSTEMD_AUTO_ENABLE = "disable"

do_install:append() {
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/debian/watchdog.service ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/debian/wd_keepalive.service ${D}${systemd_system_unitdir}

	install -Dm 0755 ${UNPACKDIR}/watchdog.init ${D}/${sysconfdir}/init.d/watchdog
	install -Dm 0755 ${UNPACKDIR}/wd_keepalive.init ${D}${sysconfdir}/init.d/wd_keepalive

	# watchdog.conf is provided by the watchdog-config recipe
	rm ${D}${sysconfdir}/watchdog.conf
}

PACKAGES =+ "${PN}-keepalive"

FILES:${PN}-keepalive = " \
    ${sysconfdir}/init.d/wd_keepalive \
    ${systemd_system_unitdir}/wd_keepalive.service \
    ${sbindir}/wd_keepalive \
"

RDEPENDS:${PN} += "${PN}-config"
RRECOMMENDS:${PN} += "kernel-module-softdog"

RDEPENDS:${PN}-keepalive += "${PN}-config"
RCONFLICTS:${PN}-keepalive += "${PN}"
RRECOMMENDS:${PN}-keepalive += "kernel-module-softdog"
