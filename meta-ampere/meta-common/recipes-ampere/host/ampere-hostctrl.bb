SUMMARY = "Ampere Computing LLC Host Control Implementation"
DESCRIPTION = "A host control implementation suitable for Ampere Computing LLC's systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} = "bash"
S = "${WORKDIR}"

SRC_URI = " \
           file://ampere-host-force-reset@.service \
           file://ampere-host-on-host-check@.service \
           file://ampere_host_check.sh \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
                         ampere-host-force-reset@.service \
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

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_host_check.sh ${D}/${sbindir}/
}
