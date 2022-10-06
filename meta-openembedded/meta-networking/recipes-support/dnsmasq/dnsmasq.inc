SUMMARY = "Lightweight, easy to configure DNS forwarder and DHCP server"
HOMEPAGE = "http://www.thekelleys.org.uk/dnsmasq/doc.html"
SECTION = "net"
# GPLv3 was added in version 2.41 as license option
LICENSE = "GPL-2.0-only | GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-v3;md5=d32239bcb673463ab874e80d47fae504 \
                    "

#at least versions 2.69 and prior are moved to the archive folder on the server
SRC_URI = "http://www.thekelleys.org.uk/dnsmasq/${@['archive/', ''][float(d.getVar('PV').split('.')[1]) > 69]}dnsmasq-${PV}.tar.gz;name=dnsmasq-${PV} \
           file://init \
           file://dnsmasq.conf \
           file://dnsmasq-resolvconf.service \
           file://dnsmasq-noresolvconf.service \
           file://dnsmasq-resolved.conf \
"

inherit pkgconfig update-rc.d systemd

INITSCRIPT_NAME = "dnsmasq"
INITSCRIPT_PARAMS = "defaults"

PACKAGECONFIG ?= ""
PACKAGECONFIG[dbus] = ",,dbus"
PACKAGECONFIG[idn] = ",,libidn"
PACKAGECONFIG[conntrack] = ",,libnetfilter-conntrack"
PACKAGECONFIG[lua] = ",,lua"
PACKAGECONFIG[resolvconf] = ",,,resolvconf"
EXTRA_OEMAKE = "\
    'COPTS=${@bb.utils.contains('PACKAGECONFIG', 'dbus', '-DHAVE_DBUS', '', d)} \
           ${@bb.utils.contains('PACKAGECONFIG', 'idn', '-DHAVE_IDN', '', d)} \
           ${@bb.utils.contains('PACKAGECONFIG', 'conntrack', '-DHAVE_CONNTRACK', '', d)} \
           ${@bb.utils.contains('PACKAGECONFIG', 'lua', '-DHAVE_LUASCRIPT', '', d)}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'resolvconf', 'file://dnsmasq.resolvconf file://99_dnsmasq file://dnsmasq-resolvconf-helper', '', d)}"

do_compile:append() {
    # build dhcp_release
    cd ${S}/contrib/lease-tools
    oe_runmake
}

do_install () {
    oe_runmake "PREFIX=${D}${prefix}" \
               "BINDIR=${D}${bindir}" \
               "MANDIR=${D}${mandir}" \
               install
    install -d ${D}${sysconfdir}/ ${D}${sysconfdir}/init.d ${D}${sysconfdir}/dnsmasq.d
    install -m 644 ${WORKDIR}/dnsmasq.conf ${D}${sysconfdir}/
    install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/dnsmasq

    install -d ${D}${systemd_unitdir}/system

    if [ "${@bb.utils.filter('PACKAGECONFIG', 'resolvconf', d)}" ]; then
        install -m 0644 ${WORKDIR}/dnsmasq-resolvconf.service ${D}${systemd_unitdir}/system/dnsmasq.service
    else
        install -m 0644 ${WORKDIR}/dnsmasq-noresolvconf.service ${D}${systemd_unitdir}/system/dnsmasq.service
    fi
    install -d ${D}${sysconfdir}/systemd/resolved.conf.d/
    install -m 0644 ${WORKDIR}/dnsmasq-resolved.conf ${D}${sysconfdir}/systemd/resolved.conf.d/

    install -m 0755 ${S}/contrib/lease-tools/dhcp_release ${D}${bindir}

    if [ "${@bb.utils.filter('PACKAGECONFIG', 'dbus', d)}" ]; then
        install -d ${D}${sysconfdir}/dbus-1/system.d
        install -m 644 dbus/dnsmasq.conf ${D}${sysconfdir}/dbus-1/system.d/
    fi
    if [ "${@bb.utils.filter('PACKAGECONFIG', 'resolvconf', d)}" ]; then
        install -d ${D}${sysconfdir}/resolvconf/update.d/
        install -m 0755 ${WORKDIR}/dnsmasq.resolvconf ${D}${sysconfdir}/resolvconf/update.d/dnsmasq

        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${WORKDIR}/99_dnsmasq ${D}${sysconfdir}/default/volatiles
        install -m 0755 ${WORKDIR}/dnsmasq-resolvconf-helper ${D}${bindir}
    fi
}

CONFFILES:${PN} = "${sysconfdir}/dnsmasq.conf"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "dnsmasq.service"
