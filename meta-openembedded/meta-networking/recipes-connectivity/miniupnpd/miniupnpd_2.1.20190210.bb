SUMMARY = "Lightweight UPnP IGD daemon"
DESCRIPTION = "The miniUPnP daemon is an UPnP IGD (internet gateway device) \
which provide NAT traversal services to any UPnP enabled client on \
the network."

SECTION = "networking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91ac00c6b9f5c106e89291e196fe0234"

inherit autotools gettext pkgconfig systemd

DEPENDS += "iptables net-tools util-linux libmnl libnetfilter-conntrack"

SRC_URI = "http://miniupnp.tuxfamily.org/files/download.php?file=${BP}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://miniupnpd.service \
           file://0001-Add-OpenEmbedded-cross-compile-case.patch \
           "
SRC_URI[md5sum] = "03b00c27106835e728a3b858ecf83390"
SRC_URI[sha256sum] = "1aaecd25cf152d99557dfe80c7508af9cb06e97ecad4786ce5dafb4c958d196b"

IPV6 = "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', '--ipv6', '', d)}"

do_configure_prepend() {
   echo "${@d.getVar('DISTRO_VERSION')}" > ${S}/os.openembedded
}

do_compile() {
    cd ${S}
    CONFIG_OPTIONS="${IPV6} --leasefile --vendorcfg" oe_runmake -f Makefile.linux
}

do_install() {
    install -d ${D}/${sbindir}
    install ${S}/miniupnpd ${D}/${sbindir}
    install -d ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/iptables_init.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/iptables_removeall.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/ip6tables_init.sh ${D}/${sysconfdir}/${BPN}
    install ${S}/netfilter/ip6tables_removeall.sh ${D}/${sysconfdir}/${BPN}
    install -m 0644 -b ${S}/miniupnpd.conf ${D}/${sysconfdir}/${BPN}
    install -d ${D}/${sysconfdir}/init.d
    install ${S}/linux/miniupnpd.init.d.script ${D}/${sysconfdir}/init.d/miniupnpd

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/miniupnpd.service ${D}${systemd_unitdir}/system/
}

SYSTEMD_SERVICE_${PN} = "miniupnpd.service"
