FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:rpi = " \
    file://fw_env.config \
"

SRC_URI:append:rpi = " file://0001-rpi-always-set-fdt_addr-with-firmware-provided-FDT-address.patch"

DEPENDS:append:rpi = " u-boot-default-script"

do_install:append:rpi () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}

# Temporary avoid Raspberry Pi 5 because U-Boot has not been ported yet
COMPATIBLE_MACHINE:raspberrypi5 = "(-)"
