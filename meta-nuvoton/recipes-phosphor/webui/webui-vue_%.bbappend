FILESEXTRAPATHS:append:nuvoton := ":${THISDIR}/${PN}"

SRC_URI:append:nuvoton = " file://0001-novnc-add-16-bit-hextile-support-for-nuvoton-ece-eng.patch"
SRC_URI:append:nuvoton = " file://0002-Add-Nuvoton-MCU-firmware-support.patch"
