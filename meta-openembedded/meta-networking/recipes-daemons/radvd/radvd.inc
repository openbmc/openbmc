SUMMARY = "IPv6 router advertisement daemon"
DESCRIPTION = "radvd is the router advertisement daemon for IPv6. It \
listens to router solicitations and sends router \
advertisements as described in RFC 2461, Neighbor \
Discovery for IP Version 6 (IPv6). With these \
advertisements hosts can automatically configure their \
addresses and some other parameters. They also can \
choose a default router based on these advertisements."
HOMEPAGE = "http://www.litech.org/radvd/"
SECTION = "net"
DEPENDS = "flex-native bison-native libdaemon "

# License is BSD-Style (with advertising clause) but also has an additional 0th clause
LICENSE = "radvd"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=73ebbf7182ae996e65e8fadc9a8c45db"

SRC_URI = "http://v6web.litech.org/radvd/dist/radvd-${PV}.tar.gz \
           file://radvd.init \
           file://radvd.service \
           file://volatiles.03_radvd \
           file://radvd.default \
           file://radvd.conf"

inherit autotools useradd pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "radvd.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install_append () {
    install -m 0755 -d ${D}${sysconfdir}/init.d \
                       ${D}${sysconfdir}/default/volatiles \
                       ${D}${docdir}/radvd
    # Install init script and volatiles
    install -m 0755 ${WORKDIR}/radvd.init ${D}${sysconfdir}/init.d/radvd
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/radvd
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/radvd
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/radvd
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/radvd

    install -m 0644 ${WORKDIR}/volatiles.03_radvd ${D}${sysconfdir}/default/volatiles/03_radvd

    # Install systemd service files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/radvd.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' \
           -e 's#@SBINDIR@#${sbindir}#g' \
           -e 's#@BASE_BINDIR@#${base_bindir}#g' ${D}${systemd_unitdir}/system/radvd.service

    # Install default environment file
    install -m 0644 ${WORKDIR}/radvd.default ${D}${sysconfdir}/default/radvd

    # Documentation
    for i in radvd.conf.example README; do \
        install -m 0644 ${S}/$i ${D}${docdir}/radvd; \
    done

    install -m 0644 ${WORKDIR}/radvd.conf ${D}${sysconfdir}/radvd.conf
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/run/radvd/ -M -g nogroup radvd"

pkg_postinst_${PN} () {
    if [ -z "$D" -a -x /etc/init.d/populate-volatile.sh ]; then
        /etc/init.d/populate-volatile.sh update
    fi
}
