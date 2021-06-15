SUMMARY = "Phosphor systemd configuration overrides"
DESCRIPTION = "Overrides for systemd and its applications"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "systemd"

SRC_URI += "file://service-restart-policy.conf"
SRC_URI += "file://journald-maxlevel-policy.conf"

FILES_${PN} += "${systemd_unitdir}/system.conf.d/service-restart-policy.conf"
FILES_${PN} += "${systemd_unitdir}/journald.conf.d/journald-maxlevel-policy.conf"


do_install() {
        install -m 644 -D ${WORKDIR}/service-restart-policy.conf ${D}${systemd_unitdir}/system.conf.d/service-restart-policy.conf
        install -m 644 -D ${WORKDIR}/journald-maxlevel-policy.conf ${D}${systemd_unitdir}/journald.conf.d/journald-maxlevel-policy.conf
}
