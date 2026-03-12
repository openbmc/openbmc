SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gensio libyaml"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz \
    file://ser2net.service \
"

SRC_URI[sha256sum] = "6b921bc7efb1b9a8a78268d63332701902cc1c8dbac51842d46ede6ffb5fa2a4"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/ser2net/files/ser2net"

inherit autotools pkgconfig systemd

SYSTEMD_SERVICE:${PN} = "ser2net.service"

CONFFILES:${PN} += "${sysconfdir}/ser2net/ser2net.yaml"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/ser2net.service ${D}${systemd_unitdir}/system/
        sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/ser2net.service
    fi
}
