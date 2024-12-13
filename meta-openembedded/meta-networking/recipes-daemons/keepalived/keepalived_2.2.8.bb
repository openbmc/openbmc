SUMMARY = "High Availability monitor built upon LVS, VRRP and service pollers"
DESCRIPTION = "Keepalived is a routing software written in C. The main goal \
of this project is to provide simple and robust facilities for loadbalancing \
and high-availability to Linux system and Linux based infrastructures. \
Loadbalancing framework relies on well-known and widely used Linux Virtual \
Server (IPVS) kernel module providing Layer4 loadbalancing \
"
HOMEPAGE = "http://www.keepalived.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://www.keepalived.org/software/${BP}.tar.gz \
           file://0001-configure.ac-Do-not-emit-compiler-flags-into-object-.patch \
           "
SRC_URI[sha256sum] = "85882eb62974f395d4c631be990a41a839594a7e62fbfebcb5649a937a7a1bb6"
UPSTREAM_CHECK_URI = "https://github.com/acassen/keepalived/releases"

DEPENDS = "libnfnetlink openssl"

inherit autotools pkgconfig systemd

PACKAGECONFIG ??= "libnl snmp \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"
PACKAGECONFIG[libnl] = "--enable-libnl,--disable-libnl,libnl"
PACKAGECONFIG[snmp] = "--enable-snmp,--disable-snmp,net-snmp"
PACKAGECONFIG[systemd] = "--with-init=systemd --with-systemdsystemunitdir=${systemd_system_unitdir},--with-init=SYSV,systemd"

EXTRA_OEMAKE = "initdir=${sysconfdir}/init.d"

export EXTRA_CFLAGS = "${CFLAGS}"

do_configure:append() {
    sed -i -e 's|${WORKDIR}|<scrubbed>|g' ${B}/lib/config.h
}

do_install:append() {
    if [ -f ${D}${sysconfdir}/init.d/${BPN} ]; then
        chmod 0755 ${D}${sysconfdir}/init.d/${BPN}
        sed -i 's#rc.d/##' ${D}${sysconfdir}/init.d/${BPN}
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0644 ${B}/${BPN}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service
    fi
}

PACKAGE_BEFORE_PN = "${PN}-samples"

FILES:${PN} += "${datadir}/snmp/mibs/KEEPALIVED-MIB.txt"

FILES:${PN}-samples = "${sysconfdir}/keepalived/samples ${sysconfdir}/keepalived/keepalived.conf.sample"

SYSTEMD_SERVICE:${PN} = "keepalived.service"
SYSTEMD_AUTO_ENABLE ?= "disable"
