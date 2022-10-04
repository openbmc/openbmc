# Align with N1SDP-2022.06.22 release
SRCREV_edk2           = "b24306f15daa2ff8510b06702114724b33895d3c"
SRCREV_edk2-platforms = "fdaf4eb69a8b6839aecf6d3bdd938aa5c34a8a17"
PV .= "+git${SRCPV}"

# N1SDP specific EDK2 configurations
EDK2_BUILD_RELEASE = "0"
EDK2_PLATFORM      = "n1sdp"
EDK2_PLATFORM_DSC  = "Platform/ARM/N1Sdp/N1SdpPlatform.dsc"
EDK2_BIN_NAME      = "BL33_AP_UEFI.fd"

COMPATIBLE_MACHINE = "n1sdp"

# UEFI EDK2 on N1SDP is unable to detect FS2 during boot resulting in launching of
# EDK2 shell instead of launching grub. The startup.nsh will force launching of grub
EFIDIR             = "/EFI/BOOT"
EFI_BOOT_IMAGE     = "bootaa64.efi"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/edk2-platforms:"

SRC_URI:append = "\
    file://0001-Platform-ARM-N1sdp-Add-support-to-parse-NT_FW_CONFIG.patch;patchdir=edk2-platforms \
    file://0002-Platform-ARM-N1Sdp-Fix-RemoteDdrSize-cast.patch;patchdir=edk2-platforms            \
    file://0003-Platform-ARM-N1Sdp-Modify-the-IRQ-ID-of-Debug-UART-a.patch;patchdir=edk2-platforms \
"

do_deploy:append() {
    EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
    printf 'FS2:%s\%s\n' "$EFIPATH" "${EFI_BOOT_IMAGE}" > ${DEPLOYDIR}/startup.nsh
}
