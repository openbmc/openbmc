FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://fw_env.config"
SRC_URI += "file://config.patch"
SRC_URI += "file://aspeednic-mac-stop.patch"

# Do not install u-boot in rootfs
#do_install[postfuncs] += "remove_uboot_from_rootfs"
#remove_uboot_from_rootfs() {
#    rm -rf ${D}/boot/u-boot*
#}
