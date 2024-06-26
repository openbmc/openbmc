SUMMARY = "Initialize system state"
DESCRIPTION = "Initialize system state"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit pkgconfig
inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"

SRC_URI += " \
    file://greatlakes-system-state-init \
    file://greatlakes-system-state-init@.service \
    "

do_install() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/greatlakes-system-state-init ${D}${libexecdir}/${PN}/
}

TGT = "${SYSTEMD_DEFAULT_TARGET}"
GREATLAKES_SYS_ST_INIT_INSTFMT="../greatlakes-system-state-init@.service:${TGT}.wants/greatlakes-system-state-init@{0}.service"

SYSTEMD_SERVICE:${PN} += "greatlakes-system-state-init@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'GREATLAKES_SYS_ST_INIT_INSTFMT', 'OBMC_HOST_INSTANCES')}"
