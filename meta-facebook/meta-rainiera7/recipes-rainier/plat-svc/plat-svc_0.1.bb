LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI += " \
    file://rainier-sys-init.service \
    file://rainier-early-sys-init \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    rainier-sys-init.service \
    "

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/rainier-early-sys-init ${D}${libexecdir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/rainier-sys-init.service ${D}${systemd_system_unitdir}
}

