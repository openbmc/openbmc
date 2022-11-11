SUMMARY = "Connection tracking userspace tools for Linux"
SECTION = "net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

DEPENDS = "libnfnetlink libnetfilter-conntrack libnetfilter-cttimeout \
           libnetfilter-cthelper libnetfilter-queue bison-native libtirpc"

EXTRA_OECONF += "LIBS=-ltirpc CPPFLAGS=-I${STAGING_INCDIR}/tirpc"

SRC_URI = "http://www.netfilter.org/projects/conntrack-tools/files/conntrack-tools-${PV}.tar.bz2 \
    file://conntrack-failover \
    file://init \
"
SRC_URI[sha256sum] = "099debcf57e81690ced57f516b493588a73518f48c14d656f823b29b4fc24b5d"

inherit autotools update-rc.d pkgconfig systemd

PACKAGECONFIG ?= "cthelper cttimeout \
		  ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[cthelper] = "--enable-cthelper,--disable-cthelper"
PACKAGECONFIG[cttimeout] = "--enable-cttimeout,--disable-cttimeout"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

INITSCRIPT_NAME = "conntrackd"

do_install:append() {
	install -d ${D}/${sysconfdir}/conntrackd
	install -d ${D}/${sysconfdir}/init.d
	install -m 0644 ${S}/doc/sync/ftfw/conntrackd.conf ${D}/${sysconfdir}/conntrackd/conntrackd.conf.sample
	install -m 0755 ${WORKDIR}/conntrack-failover ${D}/${sysconfdir}/init.d/conntrack-failover
	install -m 0755 ${WORKDIR}/init ${D}/${sysconfdir}/init.d/conntrackd

	# Fix hardcoded paths in scripts
	sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}/${sysconfdir}/init.d/conntrack-failover ${D}/${sysconfdir}/init.d/conntrackd
	sed -i 's!/etc/!${sysconfdir}/!g' ${D}/${sysconfdir}/init.d/conntrack-failover ${D}/${sysconfdir}/init.d/conntrackd
	sed -i 's!/var/!${localstatedir}/!g' ${D}/${sysconfdir}/init.d/conntrack-failover ${D}/${sysconfdir}/init.d/conntrackd ${D}/${sysconfdir}/conntrackd/conntrackd.conf.sample
	sed -i 's!^export PATH=.*!export PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}/${sysconfdir}/init.d/conntrackd
}

# fix error message: Do not forget that you need *root* or CAP_NET_ADMIN capabilities ;-)
pkg_postinst:${PN} () {
	setcap cap_net_admin+ep "$D/${sbindir}/conntrack"
}
PACKAGE_WRITE_DEPS += "libcap-native"
