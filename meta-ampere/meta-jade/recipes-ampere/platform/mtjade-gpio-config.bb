SUMMARY = "Ampere Computing LLC Utilities"
DESCRIPTION = "Ampere Utilities for Mt.Jade systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI = " \
          file://ampere_gpio_utils.sh \
          "

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

SYSTEMD_PACKAGES = "${PN}"

SYSTEMD_SERVICE:${PN} = " \
        ampere-gpio-poweroff@.service \
        ampere-gpio-poweron@.service \
        "

# chassis power on
CHASSIS_POWERON_SVC = "ampere-gpio-poweron@.service"
CHASSIS_POWERON_INSTMPL = "ampere-gpio-poweron@{0}.service"
CHASSIS_POWERON_TGTFMT = "obmc-chassis-poweron@{0}.target"
CHASSIS_POWERON_FMT = "../${CHASSIS_POWERON_SVC}:${CHASSIS_POWERON_TGTFMT}.requires/${CHASSIS_POWERON_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'CHASSIS_POWERON_FMT', 'OBMC_CHASSIS_INSTANCES')}"

# chassis power off
CHASSIS_POWEROFF_SVC = "ampere-gpio-poweroff@.service"
CHASSIS_POWEROFF_INSTMPL = "ampere-gpio-poweroff@{0}.service"
CHASSIS_POWEROFF_TGTFMT = "obmc-chassis-poweroff@{0}.target"
CHASSIS_POWEROFF_FMT = "../${CHASSIS_POWEROFF_SVC}:${CHASSIS_POWEROFF_TGTFMT}.requires/${CHASSIS_POWEROFF_INSTMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'CHASSIS_POWEROFF_FMT', 'OBMC_CHASSIS_INSTANCES')}"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${UNPACKDIR}/ampere_gpio_utils.sh ${D}/${sbindir}/
}

