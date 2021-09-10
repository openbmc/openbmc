SUMMARY = "Lightweight UPnP IGD daemon"
DESCRIPTION = "The miniUPnP daemon is an UPnP IGD (internet gateway device) \
which provide NAT traversal services to any UPnP enabled client on \
the network."

SECTION = "networking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a1ed15843ce66639bcf9f109cf247870"

inherit autotools gettext pkgconfig systemd

DEPENDS += "iptables net-tools util-linux libmnl libnetfilter-conntrack"

SRC_URI = "http://miniupnp.tuxfamily.org/files/download.php?file=${BP}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://miniupnpd.service \
           file://0001-Add-OpenEmbedded-cross-compile-case.patch \
           "
SRC_URI[md5sum] = "340789edd49c113afe37834cc901a1e8"
SRC_URI[sha256sum] = "218fad7af31f3c22fb4c9db28a55a2a8b5067d41f5b38f52008a057a00d2206d"

IPV6 = "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', '--ipv6', '', d)}"

do_configure:prepend() {
   echo "${@d.getVar('DISTRO_VERSION')}" > ${S}/os.openembedded
}

do_compile() {
    cd ${S}
    CONFIG_OPTIONS="${IPV6} --leasefile --vendorcfg" oe_runmake -f Makefile.linux config.h
    CONFIG_OPTIONS="${IPV6} --leasefile --vendorcfg" oe_runmake -f Makefile.linux
}

do_install() {
    install -d ${D}/${sbindir}
    install ${S}/miniupnpd ${D}/${sbindir}
    install -d ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/iptables_init.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/iptables_removeall.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/ip6tables_init.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/miniupnpd_functions.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/ip6tables_removeall.sh ${D}/${sysconfdir}/${BPN}
    install -m 0644 -b ${S}/miniupnpd.conf ${D}/${sysconfdir}/${BPN}
    install -d ${D}/${sysconfdir}/init.d
    install ${S}/linux/miniupnpd.init.d.script ${D}/${sysconfdir}/init.d/miniupnpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/miniupnpd.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_SERVICE:${PN} = "miniupnpd.service"
