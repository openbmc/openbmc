LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4325afd396febcb659c36b49533135d4"

PV = "0.1.0+git"

SRC_URI = " \
    git://github.com/mstpd/mstpd;branch=master;protocol=https \
    file://bridge-stp \
    file://mstpd.service \
"
SRCREV = "181c453fc1a00573e19f14960dcc54ad84beea7c"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+){2,})"

inherit autotools pkgconfig systemd

PACKAGES =+ "${PN}-mstpd"
FILES:${PN}-mstpd = "${sbindir}/mstpd ${sbindir}/mstpctl ${sbindir}/bridge-stp"

SYSTEMD_PACKAGES = "${PN}-mstpd"
SYSTEMD_SERVICE:${PN}-mstpd = "mstpd.service"

do_install:append() {
    rm -rf ${D}${libexecdir} ${D}${libdir}/NetworkManager
    rmdir ${D}${libdir} || true

    install -d -m 0755 ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/bridge-stp ${D}${sbindir}

    install -d -m 0755 ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/mstpd.service ${D}${systemd_system_unitdir}
}
