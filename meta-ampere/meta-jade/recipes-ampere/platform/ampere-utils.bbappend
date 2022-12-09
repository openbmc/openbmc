FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                  file://gpio-defs.sh \
                  file://gpio-lib.sh \
                  file://ampere_power_util.sh \
                  file://ampere_firmware_upgrade.sh \
                  file://ampere_flash_bios.sh \
                  file://ampere_driver_binder.sh \
                 "

do_install:append() {
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/gpio-lib.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/gpio-defs.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_power_util.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_firmware_upgrade.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_flash_bios.sh ${D}/${sbindir}/
    install -m 0755 ${WORKDIR}/ampere_driver_binder.sh ${D}/${sbindir}/
}
