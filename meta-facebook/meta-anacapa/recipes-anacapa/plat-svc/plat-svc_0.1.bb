LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI:append = " \
    file://nic-temp-read \
    file://nic-temp-read.service \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    nic-temp-read.service \
    "

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install() {
    PLATSVC_LIBEXECDIR="${D}${libexecdir}/plat-svc"
    install -d ${PLATSVC_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/nic-temp-read ${PLATSVC_LIBEXECDIR}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/nic-temp-read.service ${D}${systemd_system_unitdir}
}
