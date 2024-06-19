SUMMARY = "Userspace logging daemon for netfilter/iptables"
DESCRIPTION = "ulogd-2.x provides a flexible, almost universal logging daemon for \
netfilter logging. This encompasses both packet-based logging (logging of \
policy violations) and flow-based logging, e.g. for accounting purpose."
HOMEPAGE = "https://www.netfilter.org/projects/ulogd/index.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

DEPENDS = "libnfnetlink"
PROVIDES = "ulogd"

PV .= "+git"

SRC_URI = "git://git.netfilter.org/ulogd2;branch=master \
           file://ulogd.init \
           file://ulogd.service \
"
SRCREV = "79aa980f2df9dda0c097e8f883a62f414b9e5138"

S = "${WORKDIR}/git"

inherit autotools manpages pkgconfig systemd update-rc.d

PACKAGECONFIG ?= "dbi json nfacct nfct nflog pcap sqlite3 ulog"
PACKAGECONFIG[dbi] = "--enable-dbi,--disable-dbi,libdbi"
PACKAGECONFIG[json] = "--enable-json,--disable-json,jansson"
PACKAGECONFIG[manpages] = ""
PACKAGECONFIG[mysql] = "--enable-mysql,--disable-mysql,mysql5"
PACKAGECONFIG[nfacct] = "--enable-nfacct,--disable-nfacct,libnetfilter-acct"
PACKAGECONFIG[nfct] = "--enable-nfct,--disable-nfct,libnetfilter-conntrack"
PACKAGECONFIG[nflog] = "--enable-nflog,--disable-nflog,libnetfilter-log"
PACKAGECONFIG[pcap] = "--enable-pcap,--disable-pcap,libpcap"
PACKAGECONFIG[pgsql] = "--enable-pgsql,--disable-pgsql,postgresql"
PACKAGECONFIG[sqlite3] = "--enable-sqlite3,--disable-sqlite3,sqlite3"
PACKAGECONFIG[ulog] = "--enable-ulog,--disable-ulog"

do_install:append () {
	install -d ${D}${sysconfdir}
	install -m 0644 ${B}/ulogd.conf ${D}${sysconfdir}/ulogd.conf

	install -d ${D}${mandir}/man8
	install -m 0644 ${S}/ulogd.8 ${D}${mandir}/man8/ulogd.8

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${UNPACKDIR}/ulogd.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_system_unitdir}/ulogd.service

	install -d ${D}${sysconfdir}/init.d
	install -m 755 ${UNPACKDIR}/ulogd.init ${D}${sysconfdir}/init.d/ulogd
}

PACKAGES += "${PN}-plugins"
ALLOW_EMPTY:${PN}-plugins = "1"

PACKAGES_DYNAMIC += "^${PN}-plugin-.*$"
NOAUTOPACKAGEDEBUG = "1"

CONFFILES:${PN} = "${sysconfdir}/ulogd.conf"
RRECOMMENDS:${PN} += "${PN}-plugins"

FILES:${PN}-dbg += "${sbindir}/.debug"

python split_ulogd_libs () {
    libdir = d.expand('${libdir}/ulogd')
    dbglibdir = os.path.join(libdir, '.debug')

    split_packages = do_split_packages(d, libdir, r'^ulogd_.*\_([A-Z0-9]*).so', '${PN}-plugin-%s', 'ulogd2 %s plugin', prepend=True)
    split_dbg_packages = do_split_packages(d, dbglibdir, r'^ulogd_.*\_([A-Z0-9]*).so', '${PN}-plugin-%s-dbg', 'ulogd2 %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')

    if split_packages:
        pn = d.getVar('PN')
        d.setVar('RRECOMMENDS:' + pn + '-plugins', ' '.join(split_packages))
        d.appendVar('RRECOMMENDS:' + pn + '-dbg', ' ' + ' '.join(split_dbg_packages))
}
PACKAGESPLITFUNCS:prepend = "split_ulogd_libs "

SYSTEMD_SERVICE:${PN} = "ulogd.service"

INITSCRIPT_NAME = "ulogd"
INITSCRIPT_PARAMS = "defaults"
