LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"

SRC_URI += " \
    file://marvell-switch-init.service \
    file://marvell-switch-init \
    file://marvell-switch-poe-init.service \
    file://marvell-switch-poe-init \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    marvell-switch-init.service \
    marvell-switch-poe-init.service \
    "

do_install() {
    LIBEXECDIR_PN="${D}${libexecdir}/${PN}"
    install -d ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/marvell-switch-init ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/marvell-switch-poe-init ${LIBEXECDIR_PN}
}
