FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:rpi = " \
    file://fw_env.config \
"

SRC_URI:append:raspberrypi4 = " file://maxsize.cfg"

DEPENDS:append:rpi = " u-boot-default-script"

do_configure:append:raspberrypi5() {
    # Remove any existing CONFIG_BOOTDELAY= lines
    sed -i '/^CONFIG_BOOTDELAY=/d' "${B}/.config"
    # Disable U-Boot interrupt timeout to avoid
    # boot issues without a connected debug UART
    # This is a known U-Boot issue discussed in:
    # https://bugzilla.opensuse.org/show_bug.cgi?id=1251192
    # https://lists.denx.de/pipermail/u-boot/2025-January/576305.html
    echo "CONFIG_BOOTDELAY=-2" >> ${B}/.config
}

do_install:append:rpi () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
