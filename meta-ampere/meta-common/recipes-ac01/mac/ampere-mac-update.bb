SUMMARY = "Ampere Computing LLC Update MAC Address from FRU Inventory Information"
DESCRIPTION = "Update MAC Address from FRU Inventory Information for Ampere systems"
PR = "r1"

LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS = "systemd"
RDEPENDS_${PN} = "bash"

FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://ampere_update_mac.sh \
           "

SYSTEMD_SERVICE_${PN} = "ampere_update_mac.service"

do_install_append() {
    install -d ${D}/${sbindir}
    install -m 755 ${WORKDIR}/ampere_update_mac.sh ${D}/${sbindir}
}
