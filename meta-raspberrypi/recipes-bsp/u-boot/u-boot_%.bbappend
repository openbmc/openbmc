FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:rpi = " \
    file://fw_env.config \
"

# special fix for raspberrypi-cm3
SRC_URI:append:raspberrypi-cm3 = " file://0001-dm-core-Move-ofdata_to_platdata-call-earlier.patch"

DEPENDS:append:rpi = " u-boot-default-script"

do_install:append:rpi () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
