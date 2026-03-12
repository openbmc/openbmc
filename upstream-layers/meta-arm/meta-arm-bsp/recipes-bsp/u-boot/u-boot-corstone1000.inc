# Corstone1000 specific U-boot support

DEPENDS:append = " openssl-native efitools-native"
CORSTONE1000_DEVICE_TREE:corstone1000-mps3 = "arm/corstone1000-mps3"
CORSTONE1000_DEVICE_TREE:corstone1000-fvp = "arm/corstone1000-fvp"
EXTRA_OEMAKE:append = ' DEVICE_TREE=${CORSTONE1000_DEVICE_TREE}'

UBOOT_CONFIG ??= "EFI"
UBOOT_CONFIG[EFI] = "corstone1000_defconfig"
UBOOT_ENTRYPOINT  = "0x80000000"
UBOOT_LOADADDRESS = "0x80000000"
UBOOT_BOOTARGS = "${LINUX_KERNEL_ARGS} loglevel=9"
UBOOT_ARCH = "arm"
UBOOT_EXTLINUX = "0"

SYSROOT_DIRS:append = " /boot"

# FWU patches
SRC_URI:append = " \
    file://0001-arm_ffa-Add-NULL-pointer-check-to-the-uclass-driver-.patch \
    file://0002-arm_ffa-Add-FFA_MEM_SHARE-support.patch                    \
    file://0003-arm_ffa-Add-FFA_MEM_RECLAIM-support.patch                  \
    file://0004-arm_ffa-sandbox-Replace-the-emulator-error-log-with-.patch \
    file://0005-arm_ffa-sandbox-Improve-the-readability-of-clearing-.patch \
    file://0006-arm_ffa-sandbox-Add-FFA_MEM_SHARE-emulation.patch          \
    file://0007-arm_ffa-sandbox-Add-FFA_MEM_SHARE-tests.patch              \
    file://0008-arm_ffa-sandbox-Add-FFA_MEM_RECLAIM-emulation.patch        \
    file://0009-arm_ffa-sandbox-Add-FFA_MEM_RECLAIM-tests.patch            \
    file://0010-fwu_arm_psa-Initialize-the-update-agent.patch              \
    file://0011-fwu_arm_psa-Read-the-FWU-directory-through-get_image.patch \
    file://0012-fwu_arm_psa-Add-staging-ABIs.patch                         \
    file://0013-efi_loader-fwu_arm_psa-Add-set_image-and-get_image_i.patch \
    file://0014-efi_loader-fwu_arm_psa-Keep-the-FMP-payload-header.patch   \
    file://0015-efi_loader-fwu_arm_psa-Skip-accepting-the-payload-af.patch \
    file://0016-efi_loader-fwu-fwu_arm_psa-Disable-trial-state-handl.patch \
    file://0017-fwu_arm_psa-Add-FWU-acceptance-mechanism.patch             \
    file://0018-fwu_arm_psa-Add-ESRT-support.patch                         \
    file://0019-fwu_arm_psa-Add-ExitBootService-notification-handler.patch \
    file://0020-efi_loader-capsule-Add-runtime-capsule-flags-checks.patch  \
    file://0021-fwu_arm_psa-corstone1000-Enable-FWU-support.patch          \
    file://0022-fwu_arm_psa-corstone1000-Perform-bank-logic-when-rea.patch \
    file://0023-fwu_arm_psa-corstone1000-Notify-SE-Proxy-SP-on-ExitB.patch \
    file://0024-fwu_arm_psa-corstone1000-Set-Boot0001-for-on-disk-FW.patch \
    "
# Other features
SRC_URI:append = " \
    file://0025-corstone1000-set-CONFIG_FFA_SHARED_MM_BUF_ADDR.patch \
    file://0026-corstone1000-Enable-MMC-for-FVP.patch \
    file://0027-corstone1000-Enable-secure-boot-configs.patch \
    file://0028-corstone1000-Enable-EFI-set_time-config.patch \
    file://0029-corstone1000-Enable-set-print-EFI-variables.patch \
    file://0030-corstone1000-Enable-virtio-net-support.patch \
    file://0031-arm-corstone1000-Fix-unrecognized-filesystem-type.patch \
    file://0032-corstone1000-dts-Add-external-system-node.patch \
    file://0033-arm-bsp-u-boot-dts-Reserve-memory-for-RSS-comm-point.patch \
    "

# Purging device tree nodes
SRC_URI:append = " \
    file://0034-dt-Provide-a-way-to-remove-non-compliant-nodes-and-p.patch \
    file://0035-corstone1000-Purge-U-Boot-specific-DT-nodes.patch          \
    ${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000-extsys', \
                         '', 'file://0036-corstone1000-purge-remoteproc-DTS-node.patch' , d)} \
    "

# Add OF_UPSTREAM support
SRC_URI:append = " \
    file://0037-corstone1000-enable-OF_UPSTREAM-device-tree-support.patch \
"

# Use 32 bit cells for reserved-memory node in dts
SRC_URI:append = " \
    file://0038-corstone1000-dts-use-32-bit-cells-for-reserved-memor.patch \
"

# Add Cortex-a320 support
SRC_URI:append = " \
    file://0039-corstone1000-Add-Cortex-A320-support-on-FVP.patch \
"

# Add Cortex-a320 specific configurations
SRC_URI:append:cortexa320 = " \
    file://corstone1000-a320.cfg \
"

uboot_configure_config:append() {
   openssl req -x509 -sha256 -newkey rsa:2048 -subj /CN=CRT/ -keyout ${B}/CRT.key -out ${S}/CRT.crt -nodes -days 365
}

FILES:${PN} += "/corstone1000_capsule_*"
uboot_install_config:append() {
   install -D -p -m 0644 ${S}/CRT.crt ${D}/corstone1000_capsule_cert.crt
   install -D -p -m 0644 ${B}/CRT.key ${D}/corstone1000_capsule_key.key
}

do_deploy:append(){
   cp -Rf ${D}/corstone1000_capsule* ${DEPLOYDIR}/
}

addtask deploy after do_install
