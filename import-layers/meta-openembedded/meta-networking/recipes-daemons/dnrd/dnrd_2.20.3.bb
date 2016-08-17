SUMMARY = "A caching, forwarding DNS proxy server"
DESCRIPTION = "\
dnrd is a proxying nameserver. It forwards DNS queries to the appropriate \
nameserver, but can also act as the primary nameserver for a subnet behind \
a firewall. It also has features such as caching DNS requests, support for \
DNS servers, cache poisoning prevention, TCP support, etc.."
HOMEPAGE = "http://dnrd.sourceforge.net/"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0be67017f1c770313ad7b40e18d568f1"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://dnrd.service \
           file://dnrd.conf.sample \
           file://dnrd.init"
SRC_URI[md5sum] = "41c9b070aae8ed403fc8c2aac7ab157c"
SRC_URI[sha256sum] = "aa46e7f8736b88c1d752cf606b3990041221ce91d014e955c6b02eb2167db015"

PNBLACKLIST[dnrd] ?= "BROKEN: dnrd-2.20.3-r0 do_package: QA Issue: dnrd: Files/directories were installed but not shipped in any package:"

SYSTEMD_SERVICE_${PN} = "dnrd.service"
SYSTEMD_AUTO_ENABLE = "disable"

inherit autotools
inherit ${@base_contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','', d)}

do_install() {
    oe_runmake install DESTDIR=${D} INSTALL="install -p"

    sed -i -e 's:/etc/rc.d/init.d/functions:/etc/init.d/functions:g' \
        ${WORKDIR}/dnrd.init
    install -d -m 0755 ${D}${sysconfdir}/init.d
    install -d -m 0755 ${D}${sysconfdir}/dnrd
    install -p -m 0644 ${WORKDIR}/dnrd.conf.sample ${D}${sysconfdir}/dnrd/dnrd.conf
    install -p -m 0755 ${WORKDIR}/dnrd.init ${D}${sysconfdir}/init.d/dnrd

    if ${@base_contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d -m 0755 ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/dnrd.service ${D}${systemd_unitdir}/system
    fi
}
