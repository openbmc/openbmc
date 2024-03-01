SUMMARY = "Lightweight, easy to configure DNS forwarder and DHCP server"
HOMEPAGE = "http://www.thekelleys.org.uk/dnsmasq/doc.html"
SECTION = "net"
# GPLv3 was added in version 2.41 as license option
LICENSE = "GPL-2.0-only | GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-v3;md5=d32239bcb673463ab874e80d47fae504 \
                    "

DEPENDS += "gettext-native"

#at least versions 2.69 and prior are moved to the archive folder on the server
SRC_URI = "http://www.thekelleys.org.uk/dnsmasq/${@['archive/', ''][float(d.getVar('PV').split('.')[1]) > 69]}dnsmasq-${PV}.tar.gz \
           file://init \
           file://dnsmasq.conf \
           file://dnsmasq-resolvconf.service \
           file://dnsmasq-noresolvconf.service \
           file://dnsmasq-resolved.conf \
"
SRC_URI[sha256sum] = "8f6666b542403b5ee7ccce66ea73a4a51cf19dd49392aaccd37231a2c51b303b"

inherit pkgconfig update-rc.d systemd

INITSCRIPT_NAME = "dnsmasq"
INITSCRIPT_PARAMS = "defaults"

# dnsmasq defaults
PACKAGECONFIG ?= "auth dhcp dhcp6 dumpfile inotify ipset loop script tftp"

PACKAGECONFIG[auth] = "-DHAVE_AUTH,-DNO_AUTH"
PACKAGECONFIG[broken-rtc] = "-DHAVE_BROKEN_RTC,"
PACKAGECONFIG[conntrack] = "-DHAVE_CONNTRACK,,libnetfilter-conntrack"
PACKAGECONFIG[dbus] = "-DHAVE_DBUS,,dbus"
PACKAGECONFIG[dhcp] = "-DHAVE_DHCP,-DNO_DHCP"
PACKAGECONFIG[dhcp6] = "-DHAVE_DHCP6,-DNO_DHCP6"
PACKAGECONFIG[dnssec] = "-DHAVE_DNSSEC,,nettle"
PACKAGECONFIG[dumpfile] = "-DHAVE_DUMPFILE,-DNO_DUMPFILE"
PACKAGECONFIG[idn] = "-DHAVE_LIBIDN,,libidn,,,idn2"
PACKAGECONFIG[idn2] = "-DHAVE_LIBIDN2,,libidn2,,,idn"
PACKAGECONFIG[inotify] = "-DHAVE_INOTIFY,-DNO_INOTIFY"
PACKAGECONFIG[ipset] = "-DHAVE_IPSET,-DNO_IPSET"
PACKAGECONFIG[loop] = "-DHAVE_LOOP,-DNO_LOOP"
PACKAGECONFIG[lua] = "-DHAVE_LUASCRIPT -DHAVE_SCRIPT,,lua"
PACKAGECONFIG[nftset] = "-DHAVE_NFTSET,,nftables"
PACKAGECONFIG[no-gmp] = "-DNO_GMP,"
PACKAGECONFIG[no-id] = "-DNO_ID,"
PACKAGECONFIG[resolvconf] = ",,,resolvconf"
PACKAGECONFIG[script] = "-DHAVE_SCRIPT,-DNO_SCRIPT"
PACKAGECONFIG[tftp] = "-DHAVE_TFTP,-DNO_TFTP"
PACKAGECONFIG[ubus] = "-DHAVE_UBUS,,ubus"

DNSMASQ_LEASEFILE ?= "${localstatedir}/lib/misc/dnsmasq.leases"
DNSMASQ_CONFFILE ?= "${sysconfdir}/dnsmasq.conf"
DNSMASQ_RESOLVFILE ?= "${sysconfdir}/resolv.conf"

COPTS = "${PACKAGECONFIG_CONFARGS} \
         -DLEASEFILE=\"${DNSMASQ_LEASEFILE}\" \
         -DCONFFILE=\"${DNSMASQ_CONFFILE}\" \
         -DRESOLVFILE=\"${DNSMASQ_RESOLVFILE}\" \
         -DLOCALEDIR=\"${localedir}\""

EXTRA_OEMAKE = "\
    'COPTS=${COPTS}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'resolvconf', 'file://dnsmasq.resolvconf file://99_dnsmasq file://dnsmasq-resolvconf-helper', '', d)}"

do_compile () {
    oe_runmake all-i18n
    if ${@bb.utils.contains_any('PACKAGECONFIG', ['dhcp', 'dhcp6'], 'true', 'false', d)}; then
        # build dhcp_release
        oe_runmake -C ${S}/contrib/lease-tools
    fi
}

do_install () {
    oe_runmake "PREFIX=${D}${prefix}" \
               "BINDIR=${D}${bindir}" \
               "MANDIR=${D}${mandir}" \
               install-i18n
    install -d ${D}${sysconfdir}/ ${D}${sysconfdir}/init.d ${D}${sysconfdir}/dnsmasq.d
    install -m 644 ${WORKDIR}/dnsmasq.conf ${D}${sysconfdir}/
    install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/dnsmasq

    install -d ${D}${systemd_unitdir}/system

    if [ "${@bb.utils.filter('PACKAGECONFIG', 'resolvconf', d)}" ]; then
        install -m 0644 ${WORKDIR}/dnsmasq-resolvconf.service ${D}${systemd_unitdir}/system/dnsmasq.service
    else
        install -m 0644 ${WORKDIR}/dnsmasq-noresolvconf.service ${D}${systemd_unitdir}/system/dnsmasq.service
    fi

    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${sysconfdir}/systemd/resolved.conf.d/
        install -m 0644 ${WORKDIR}/dnsmasq-resolved.conf ${D}${sysconfdir}/systemd/resolved.conf.d/
    fi

    if [ "${@bb.utils.filter('PACKAGECONFIG', 'dhcp', d)}" ]; then
        install -m 0755 ${S}/contrib/lease-tools/dhcp_release ${D}${bindir}
        install -m 0755 ${S}/contrib/lease-tools/dhcp_lease_time ${D}${bindir}
    fi

    if [ "${@bb.utils.filter('PACKAGECONFIG', 'dhcp6', d)}" ]; then
        install -m 0755 ${S}/contrib/lease-tools/dhcp_release6 ${D}${bindir}
    fi

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
