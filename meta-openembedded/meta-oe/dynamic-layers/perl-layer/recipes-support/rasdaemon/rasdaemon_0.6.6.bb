DESCRIPTION = "Tools to provide a way to get Platform Reliability, Availability and Serviceability (RAS) reports made via the Kernel tracing events"
HOMEPAGE = "http://git.infradead.org/users/mchehab/rasdaemon.git"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d3070efe0afa3dc41608bd82c00bb0dc"

SRC_URI = "git://github.com/mchehab/rasdaemon.git;branch=master \
           file://0001-Fix-system-header-includes.patch \
           file://rasdaemon.service \
           file://init"

SRCREV = "32b978fcf73215c90ce7383580224e4295930ae7"

S = "${WORKDIR}/git"

RDEPENDS_${BPN} = "perl perl-module-file-basename perl-module-file-find perl-module-file-spec perl-module-getopt-long \
	perl-module-posix perl-module-file-glob libdbi-perl libdbd-sqlite-perl"

inherit autotools pkgconfig update-rc.d systemd

PACKAGECONFIG ??= "sqlite3 mce aer extlog devlink diskerror"
PACKAGECONFIG[sqlite3] = "--enable-sqlite3,--disable-sqlite3,sqlite3"
PACKAGECONFIG[mce] = "--enable-mce,--disable-mce"
PACKAGECONFIG[aer] = "--enable-aer,--disable-aer"
PACKAGECONFIG[extlog] = "--enable-extlog,--disable-extlog"
PACKAGECONFIG[devlink] = "--enable-devlink,--disable-devlink"
PACKAGECONFIG[diskerror] = "--enable-diskerror,--disable-diskerror"
PACKAGECONFIG[arm] = "--enable-arm,--disable-arm"
PACKAGECONFIG[hisi-ns-decode] = "--enable-hisi-ns-decode,--disable-hisi-ns-decode"
PACKAGECONFIG[non-standard] = "--enable-non-standard,--disable-non-standard"
PACKAGECONFIG[abrt-report] = "--enable-abrt-report,--disable-abrt-report"

DEPENDS_append_libc-musl = " argp-standalone"
LDFLAGS_append_libc-musl = " -largp"

do_configure_prepend () {
	( cd ${S}; autoreconf -vfi )
}

do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/rasdaemon
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/rasdaemon.service ${D}${systemd_unitdir}/system
}

FILES_${PN} += "${sbindir}/rasdaemon \
		${sysconfdir}/init.d \
		${systemd_unitdir}/system/rasdaemon.service"

SYSTEMD_SERVICE_${PN} = "rasdaemon.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "rasdaemon"
INITSCRIPT_PARAMS_${PN} = "defaults 89"
