FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://0001-smbiosmdrv2handler-add-new-ipmi-oem-cmd-for-mdrv2.patch"
