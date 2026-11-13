require recipes-bsp/uefi/edk2-firmware.inc

SRCREV_edk2           ?= "2970e5699ba6267f3384ffab20f96647578aebc8"
SRCREV_edk2-platforms ?= "c98cdf8de9bd84a3267623e64f4388e21de69512"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
        file://0001-DynamicTablesPkg-fix-SmbiosType2Genertor-build-failu.patch \
        file://0002-NetworkPkg-UefiPxeBcDxe-Fix-ICMP-buffer-overflow-in-.patch \
        file://0003-NetworkPkg-UefiPxeBcDxe-Clear-ICMP-error-buffer-befo.patch \
       "
