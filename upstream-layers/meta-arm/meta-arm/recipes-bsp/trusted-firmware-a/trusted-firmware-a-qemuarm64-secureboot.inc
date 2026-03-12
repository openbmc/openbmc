COMPATIBLE_MACHINE = "qemuarm64-secureboot"

# Enable passing TOS_FW_CONFIG from FIP package to Trusted OS.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://0001-Add-spmc_manifest-for-qemu.patch"

TFA_PLATFORM = "qemu"

# Trusted Services secure partitions require arm-ffa machine feature.
# Enabling Secure-EL1 Payload Dispatcher (SPD) in this case
TFA_SPD = "${@bb.utils.contains('MACHINE_FEATURES', 'arm-ffa', 'spmd', 'opteed', d)}"
# Configure tf-a accordingly to TS requirements if included
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'arm-ffa', ' CTX_INCLUDE_EL2_REGS=0 SPMC_OPTEE=1 ', '' , d)}"
# Cortex-A57 supports Armv8.0 (no S-EL2 execution state).
# The SPD SPMC component should run at the S-EL1 execution state.
TFA_SPMD_SPM_AT_SEL2 = "0"

TFA_UBOOT ?= "1"

TFA_INSTALL_TARGET = "flash.bin"

# When using OP-TEE SPMC specify the SPMC manifest file.
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'arm-ffa', \
    'QEMU_TOS_FW_CONFIG_DTS=${S}/plat/qemu/fdts/optee_spmc_manifest.dts', '', d)}"

do_compile:append() {
    # Create a secure flash image for booting AArch64 Qemu. See:
    # https://trustedfirmware-a.readthedocs.io/en/latest/plat/qemu.html
    dd if=${BUILD_DIR}/bl1.bin of=${BUILD_DIR}/flash.bin bs=4096 conv=notrunc
    dd if=${BUILD_DIR}/fip.bin of=${BUILD_DIR}/flash.bin seek=64 bs=4096 conv=notrunc
}

do_deploy:append(){
    # runqemu requires flash.bin to be in the deploy directory
    ln -srn ${DEPLOYDIR}/${PN}/flash.bin ${DEPLOYDIR}/flash.bin
}
