SUMMARY = "Lightweight UPnP IGD daemon"
DESCRIPTION = "The miniUPnP daemon is an UPnP IGD (internet gateway device) \
which provide NAT traversal services to any UPnP enabled client on \
the network."

SECTION = "networking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a1ed15843ce66639bcf9f109cf247870"

inherit gettext pkgconfig systemd

DEPENDS += "iptables net-tools util-linux libmnl libnetfilter-conntrack openssl"

SRC_URI = "http://miniupnp.tuxfamily.org/files/download.php?file=${BP}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://miniupnpd.service \
           file://0001-Add-OpenEmbedded-cross-compile-case.patch \
           "
SRC_URI[sha256sum] = "218fad7af31f3c22fb4c9db28a55a2a8b5067d41f5b38f52008a057a00d2206d"

UPSTREAM_CHECK_URI = "https://miniupnp.tuxfamily.org/files/"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>\d+(\.\d+)+)\.tar"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--ipv6"

EXTRA_OEMAKE = "-f Makefile.linux"

do_configure() {
    echo "${@d.getVar('DISTRO_VERSION')}" > ${S}/os.openembedded
    CONFIG_OPTIONS="--leasefile --vendorcfg ${PACKAGECONFIG_CONFARGS}" oe_runmake --always-make config.h
}

do_compile() {
    oe_runmake
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
    install -m 0644 ${UNPACKDIR}/miniupnpd.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_SERVICE:${PN} = "miniupnpd.service"
