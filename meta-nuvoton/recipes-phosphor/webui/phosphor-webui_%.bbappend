FILESEXTRAPATHS:append:nuvoton := ":${THISDIR}/${PN}"

SRC_URI:append:nuvoton = " file://0001-novnc-add-16-bit-hextile-support-for-nuvoton-ece-eng.patch"
#SRC_URI:append:nuvoton = " file://0002-support-pldm-sensors-effecters.patch"
SRC_URI:append:nuvoton = " file://0003-add-mcu-firmware-update-functionality.patch"
