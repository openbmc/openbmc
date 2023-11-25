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

FILESEXTRAPATHS:prepend := "${THISDIR}/files/n1sdp:"
SRC_URI:append = "\
    file://0001-Platform-ARM-N1sdp-Add-support-to-parse-NT_FW_CONFIG.patch;patchdir=edk2-platforms \
    file://0002-Platform-ARM-N1Sdp-Modify-the-IRQ-ID-of-Debug-UART-a.patch;patchdir=edk2-platforms \
    file://0003-Silicon-ARM-NeoverseN1Soc-Enable-SCP-QSPI-flash-regi.patch;patchdir=edk2-platforms \
    file://0004-Platform-ARM-N1Sdp-NOR-flash-library-for-N1Sdp.patch;patchdir=edk2-platforms \
    file://0005-Platform-ARM-N1Sdp-NOR-flash-Dxe-Driver-for-N1Sdp.patch;patchdir=edk2-platforms \
    file://0006-Platform-ARM-N1Sdp-Persistent-storage-for-N1Sdp.patch;patchdir=edk2-platforms \
    file://0007-Platform-ARM-N1Sdp-Enable-FaultTolerantWrite-Dxe-dri.patch;patchdir=edk2-platforms \
    file://0008-Platform-ARM-N1Sdp-manually-poll-QSPI-status-bit-aft.patch;patchdir=edk2-platforms \
    file://0009-Platform-ARM-N1Sdp-Reserve-OP-TEE-Region-from-UEFI.patch;patchdir=edk2-platforms \
"

do_deploy:append() {
    EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
    printf 'FS2:%s\%s\n' "$EFIPATH" "${EFI_BOOT_IMAGE}" > ${DEPLOYDIR}/startup.nsh
}
