SUMMARY = "Phosphor OpenBMC Boot Progress Handling Service"
DESCRIPTION = "Phosphor OpenBMC Altra Boot Progress Handling Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " \
           file://ampere_boot_progress.sh \
           file://dimm_train_fail_log.sh \
          "

SYSTEMD_PACKAGES = "${PN}"

HOST_ON_RESET_HOSTTMPL = "ampere-boot-progress.service"
HOST_ON_RESET_HOSTINSTMPL = "ampere-boot-progress.service"
HOST_ON_RESET_HOSTTGTFMT = "obmc-host-already-on@{0}.target"
HOST_ON_RESET_HOSTFMT = "../${HOST_ON_RESET_HOSTTMPL}:${HOST_ON_RESET_HOSTTGTFMT}.requires/${HOST_ON_RESET_HOSTINSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'HOST_ON_RESET_HOSTFMT', 'OBMC_HOST_INSTANCES')}"

SYSTEMD_SERVICE:${PN} += "${HOST_ON_RESET_HOSTTMPL}"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/ampere_boot_progress.sh ${D}${sbindir}/
    install -m 0755 ${UNPACKDIR}/dimm_train_fail_log.sh ${D}${sbindir}/
}

