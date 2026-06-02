LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI:append = " \
    file://hmc-datetime-sync \
    file://hmc-datetime-sync.service \
    file://hmc-datetime-sync.timer \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    hmc-datetime-sync.service \
    hmc-datetime-sync.timer \
    "

do_install() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/hmc-datetime-sync ${D}${libexecdir}/${PN}/
}

