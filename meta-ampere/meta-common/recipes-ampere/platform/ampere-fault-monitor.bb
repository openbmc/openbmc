SUMMARY = "Ampere Computing LLC Fault Monitor"
DESCRIPTION = "Monitor fault events and update fault led status for Ampere systems"
PR = "r1"

LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN} = "ampere-fault-monitor.service"

GPIO_FAULT_START_TGT = "ampere-check-gpio-fault@.service"
GPIO_FAULT_START_S0_INSTMPL = "ampere-check-gpio-fault@{0}.service"
SYSTEMD_SERVICE:${PN} += "${GPIO_FAULT_START_TGT}"

HOST_ON_STARTMIN_TGTFMT = "obmc-host-startmin@{0}.target"
GPIO_FAULT_START_S0_STARTMIN_FMT = "../${GPIO_FAULT_START_TGT}:${HOST_ON_STARTMIN_TGTFMT}.wants/${GPIO_FAULT_START_S0_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'GPIO_FAULT_START_S0_STARTMIN_FMT', 'OBMC_HOST_INSTANCES')}"

