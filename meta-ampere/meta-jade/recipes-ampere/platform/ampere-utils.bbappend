FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                  file://ampere_power_util.sh \
                  file://ampere_firmware_upgrade.sh \
                  file://ampere_flash_bios.sh \
                  file://ampere_driver_binder.sh \
                 "

do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${UNPACKDIR}/ampere_power_util.sh ${D}/${sbindir}/
    install -m 0755 ${UNPACKDIR}/ampere_firmware_upgrade.sh ${D}/${sbindir}/
    install -m 0755 ${UNPACKDIR}/ampere_flash_bios.sh ${D}/${sbindir}/
    install -m 0755 ${UNPACKDIR}/ampere_driver_binder.sh ${D}/${sbindir}/
}
