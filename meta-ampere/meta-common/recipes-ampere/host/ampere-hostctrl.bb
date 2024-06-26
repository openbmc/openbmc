SUMMARY = "Ampere Computing LLC Host Control Implementation"
DESCRIPTION = "A host control implementation suitable for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} = "bash"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI = " \
           file://ampere-host-force-reset@.service \
           file://ampere-host-on-host-check@.service \
           file://ampere_host_check.sh \
           file://obmc-power-already-on@.target \
           file://obmc-host-already-on@.target \
           file://ampere-bmc-reboot-host-check@.service \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
                         ampere-host-force-reset@.service \
                         obmc-power-already-on@.target \
                         obmc-host-already-on@.target \
                         ampere-bmc-reboot-host-check@.service \
                        "

# append force reboot
HOST_WARM_REBOOT_FORCE_TGT = "ampere-host-force-reset@.service"
HOST_WARM_REBOOT_FORCE_INSTMPL = "ampere-host-force-reset@{0}.service"
HOST_WARM_REBOOT_FORCE_TGTFMT = "obmc-host-force-warm-reboot@{0}.target"
HOST_WARM_REBOOT_FORCE_TARGET_FMT = "../${HOST_WARM_REBOOT_FORCE_TGT}:${HOST_WARM_REBOOT_FORCE_TGTFMT}.requires/${HOST_WARM_REBOOT_FORCE_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_WARM_REBOOT_FORCE_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_SERVICE:${PN} += "${HOST_WARM_REBOOT_FORCE_TGT}"

HOST_ON_RESET_HOSTTMPL = "ampere-host-on-host-check@.service"
HOST_ON_RESET_HOSTINSTMPL = "ampere-host-on-host-check@{0}.service"
HOST_ON_RESET_HOSTTGTFMT = "obmc-host-startmin@{0}.target"
HOST_ON_RESET_HOSTFMT = "../${HOST_ON_RESET_HOSTTMPL}:${HOST_ON_RESET_HOSTTGTFMT}.requires/${HOST_ON_RESET_HOSTINSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_ON_RESET_HOSTFMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_SERVICE:${PN} += "${HOST_ON_RESET_HOSTTMPL}"

# append on phosphor-wait-power-on
AMPERE_POWER_ON_TGT = "obmc-power-already-on@.target"
AMPERE_POWER_ON_INSTMPL = "obmc-power-already-on@{0}.target"
OP_WAIT_POWER_ON = "phosphor-wait-power-on@{0}.service"
AMPERE_POWER_ON_TARGET_FMT = "../${AMPERE_POWER_ON_TGT}:${OP_WAIT_POWER_ON}.wants/${AMPERE_POWER_ON_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'AMPERE_POWER_ON_TARGET_FMT', 'OBMC_HOST_INSTANCES')}"

# append on obmc-chassis-poweron but will start after phosphor-state-manager init
# host state as running
HOST_CHECK_BMC_REBOOT_HOSTTMPL = "ampere-bmc-reboot-host-check@.service"
HOST_CHECK_BMC_REBOOT_HOSTINSTMPL = "ampere-bmc-reboot-host-check@{0}.service"
HOST_CHECK_BMC_REBOOT_HOSTTGTFMT = "obmc-chassis-poweron@{0}.target"
HOST_CHECK_BMC_REBOOT_HOSTFMT = "../${HOST_CHECK_BMC_REBOOT_HOSTTMPL}:${HOST_CHECK_BMC_REBOOT_HOSTTGTFMT}.requires/${HOST_CHECK_BMC_REBOOT_HOSTINSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_CHECK_BMC_REBOOT_HOSTFMT', 'OBMC_HOST_INSTANCES')}"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${UNPACKDIR}/ampere_host_check.sh ${D}/${sbindir}/
}
