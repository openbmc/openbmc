SUMMARY = "Ampere Computing LLC System Firmware Hang Handler"
DESCRIPTION = "A host control implementation suitable for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} = "bash"
FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SYSTEMD_PACKAGES = "${PN}"

SRC_URI = " \
           file://ampere-sysfw-hang-handler.service \
           file://ampere_sysfw_hang_handler.sh \
          "

SYSTEMD_SERVICE:${PN} += "ampere-sysfw-hang-handler.service"

SYSFW_HANG_TGT = "ampere-sysfw-hang-handler.service"
SYSFW_HANG_INSTMPL = "ampere-sysfw-hang-handler.service"
AMPER_HOST_RUNNING = "obmc-host-already-on@{0}.target"
SYSFW_HANG_TARGET_FMT = "../${SYSFW_HANG_TGT}:${AMPER_HOST_RUNNING}.wants/${SYSFW_HANG_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'SYSFW_HANG_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_sysfw_hang_handler.sh ${D}/${sbindir}/
}
