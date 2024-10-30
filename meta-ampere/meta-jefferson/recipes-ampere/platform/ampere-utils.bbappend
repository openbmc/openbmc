FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

INSANE_SKIP:${PN} = "already-stripped"

SRC_URI:append = " \
                  file://ampere_firmware_upgrade.sh \
                  file://ampere_flash_bios.sh \
                  file://ampere_power_util.sh \
                 "
do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_firmware_upgrade.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_flash_bios.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_power_util.sh ${D}/${sbindir}/
}
