DESCRIPTION = "Tools to provide a way to get Platform Reliability, Availability and Serviceability (RAS) reports made via the Kernel tracing events"
HOMEPAGE = "http://git.infradead.org/users/mchehab/rasdaemon.git"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d3070efe0afa3dc41608bd82c00bb0dc"

SRC_URI = "git://github.com/mchehab/rasdaemon.git;branch=master;protocol=https \
           file://rasdaemon.service \
           file://init"

SRCREV = "4e83b848e7961af25028f3a2cecf37a63279a2bf"

S = "${WORKDIR}/git"

DEPENDS = "libtraceevent"
RDEPENDS:${BPN} = "perl perl-module-file-basename perl-module-file-find perl-module-file-spec perl-module-getopt-long \
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

DEPENDS:append:libc-musl = " argp-standalone"
LDFLAGS:append:libc-musl = " -largp"

do_install:append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/rasdaemon
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${UNPACKDIR}/rasdaemon.service ${D}${systemd_unitdir}/system
}

FILES:${PN} += "${sbindir}/rasdaemon \
		${sysconfdir}/init.d \
		${systemd_unitdir}/system/rasdaemon.service"

SYSTEMD_SERVICE:${PN} = "rasdaemon.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "rasdaemon"
INITSCRIPT_PARAMS:${PN} = "defaults 89"
