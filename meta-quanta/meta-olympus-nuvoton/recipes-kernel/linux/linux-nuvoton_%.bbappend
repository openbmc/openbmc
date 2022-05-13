FILESEXTRAPATHS:prepend := "${THISDIR}/linux-nuvoton:"

SRC_URI:append:olympus-nuvoton = " file://olympus-nuvoton.cfg"
SRC_URI:append:olympus-nuvoton = " file://vlan.cfg"
SRC_URI:append:olympus-nuvoton = " file://0001-dts-remove-mtd-uboot-read-only.patch"
