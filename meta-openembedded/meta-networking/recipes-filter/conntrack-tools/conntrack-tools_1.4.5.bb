SUMMARY = "Connection tracking userspace tools for Linux"
SECTION = "net"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

DEPENDS = "libnfnetlink libnetfilter-conntrack libnetfilter-cttimeout \
           libnetfilter-cthelper libnetfilter-queue bison-native libtirpc"

EXTRA_OECONF += "LIBS=-ltirpc CPPFLAGS=-I${STAGING_INCDIR}/tirpc"

SRC_URI = "http://www.netfilter.org/projects/conntrack-tools/files/conntrack-tools-${PV}.tar.bz2;name=tar \
    file://conntrack-failover \
    file://init \
"
SRC_URI[tar.md5sum] = "9356a0cd4df81a597ac26d87ccfebac4"
SRC_URI[tar.sha256sum] = "36c6d99c7684851d4d72e75bd07ff3f0ff1baaf4b6f069eb7244990cd1a9a462"

inherit autotools update-rc.d pkgconfig

INITSCRIPT_NAME = "conntrackd"

do_install_append() {
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
