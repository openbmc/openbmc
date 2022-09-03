# Kernel configuration and dts specific information

#
# Kernel configurations and dts (If not using Linux provided ones) are captured
# in this file. Update SRC_URI and do_patch for building images with custom dts
#

# We can't set FILESEXTRAPATHS once because of how the kernel classes search for
# config fragments. Discussion is ongoing as to whether this is the correct
# solution, or a workaround.
# https://bugzilla.yoctoproject.org/show_bug.cgi?id=14154
ARMBSPFILESPATHS := "${THISDIR}:${THISDIR}/files:"

# Arm platforms kmeta
SRC_URI_KMETA = "file://arm-platforms-kmeta;type=kmeta;name=arm-platforms-kmeta;destsuffix=arm-platforms-kmeta"
SRC_URI:append:fvp-base = " ${SRC_URI_KMETA}"
SRC_URI:append:fvp-base-arm32 = " ${SRC_URI_KMETA}"
SRC_URI:append:fvp-baser-aemv8r64 = " ${SRC_URI_KMETA}"
SRC_URI:append:juno = " ${SRC_URI_KMETA}"
SRC_URI:append:n1sdp = " ${SRC_URI_KMETA}"
SRC_URI:append:tc = " ${SRC_URI_KMETA}"
SRCREV:arm-platforms-kmeta = "6147e82375aa9df8f2a162d42ea6406c79c854c5"

#
# Corstone-500 KMACHINE
#
COMPATIBLE_MACHINE:corstone500 = "corstone500"
KBUILD_DEFCONFIG:corstone500  = "multi_v7_defconfig"
KCONFIG_MODE:corstone500 = "--alldefconfig"

#
# Corstone1000 KMACHINE
#
FILESEXTRAPATHS:prepend:corstone1000 := "${ARMBSPFILESPATHS}"
COMPATIBLE_MACHINE:corstone1000 = "${MACHINE}"
KCONFIG_MODE:corstone1000 = "--alldefconfig"
KMACHINE:corstone1000 = "corstone1000"
LINUX_KERNEL_TYPE:corstone1000 = "standard"
#disabling the rootfs cpio file compression so it is not compressed twice when bundled with the kernel
KERNEL_EXTRA_ARGS:corstone1000 += "CONFIG_INITRAMFS_COMPRESSION_NONE=y"
SRC_URI:append:corstone1000 = " \
           file://defconfig  \
           file://0001-UPSTREAM-firmware-arm_ffa-Handle-compatibility-with-.patch  \
        "

SRC_URI:append:corstone1000 = " ${@bb.utils.contains('MACHINE_FEATURES', \
                                                            'corstone1000_kernel_debug', \
                                                            'file://corstone1000_kernel_debug.cfg', \
                                                            '', \
                                                             d)}"

# Default kernel features not needed for corstone1000
# otherwise the extra kernel modules will increase the rootfs size
# corstone1000 has limited flash memory constraints
KERNEL_EXTRA_FEATURES:corstone1000 = ""
KERNEL_FEATURES:corstone1000 = ""

#
# FVP BASE KMACHINE
#
COMPATIBLE_MACHINE:fvp-base = "fvp-base"
KMACHINE:fvp-base = "fvp"
FILESEXTRAPATHS:prepend:fvp-base := "${ARMBSPFILESPATHS}"

#
# FVP BASE ARM32 KMACHINE
#
COMPATIBLE_MACHINE:fvp-base-arm32 = "fvp-base-arm32"
KMACHINE:fvp-base-arm32 = "fvp-arm32"
FILESEXTRAPATHS:prepend:fvp-base-arm32 := "${ARMBSPFILESPATHS}"
SRC_URI:append:fvp-base-arm32 = " file://0001-ARM-vexpress-enable-GICv3.patch"
# We want to use the DT in the arm64 tree but the kernel build doesn't like that, so symlink it
do_compile:prepend:fvp-base-arm32() {
    mkdir --parents ${S}/arch/arm/boot/dts/arm
    for file in fvp-base-revc.dts rtsm_ve-motherboard.dtsi rtsm_ve-motherboard-rs2.dtsi; do
        ln -fsr ${S}/arch/arm64/boot/dts/arm/$file ${S}/arch/arm/boot/dts/arm
    done
}

#
# FVP BaseR AEMv8r64 Machine
#
COMPATIBLE_MACHINE:fvp-baser-aemv8r64 = "fvp-baser-aemv8r64"
FILESEXTRAPATHS:prepend:fvp-baser-aemv8r64 := "${ARMBSPFILESPATHS}"
SRC_URI:append:fvp-baser-aemv8r64 = " file://fvp-baser-aemv8r64.dts;subdir=git/arch/arm64/boot/dts/arm"

#
# Juno KMACHINE
#
COMPATIBLE_MACHINE:juno = "juno"
KBUILD_DEFCONFIG:juno = "defconfig"
KCONFIG_MODE:juno = "--alldefconfig"
FILESEXTRAPATHS:prepend:juno := "${ARMBSPFILESPATHS}"

#
# Musca B1/S2 can't run Linux
#
COMPATIBLE_MACHINE:musca-b1 = "(^$)"
COMPATIBLE_MACHINE:musca-s1 = "(^$)"

#
# N1SDP KMACHINE
#
FILESEXTRAPATHS:prepend:n1sdp := "${THISDIR}/linux-yocto-5.15/n1sdp:"
COMPATIBLE_MACHINE:n1sdp = "n1sdp"
KBUILD_DEFCONFIG:n1sdp = "defconfig"
KCONFIG_MODE:n1sdp = "--alldefconfig"
FILESEXTRAPATHS:prepend:n1sdp := "${ARMBSPFILESPATHS}"
SRC_URI:append:n1sdp = " \
    file://0001-iommu-arm-smmu-v3-workaround-for-ATC_INV_SIZE_ALL-in.patch \
    file://0002-n1sdp-pci_quirk-add-acs-override-for-PCI-devices.patch \
    file://0003-pcie-Add-quirk-for-the-Arm-Neoverse-N1SDP-platform.patch \
    file://0004-n1sdp-pcie-add-quirk-support-enabling-remote-chip-PC.patch \
    file://0005-arm64-kpti-Whitelist-early-Arm-Neoverse-N1-revisions.patch \
    file://enable-realtek-R8169.cfg \
    file://enable-usb_conn_gpio.cfg \
    file://usb_xhci_pci_renesas.cfg \
    "
# Since we use the intree defconfig and the preempt-rt turns off some configs
# do_kernel_configcheck will display warnings. So, lets disable it.
KCONF_AUDIT_LEVEL:n1sdp:pn-linux-yocto-rt = "0"

#
# SGI575 KMACHINE
#
COMPATIBLE_MACHINE:sgi575 = "sgi575"
KBUILD_DEFCONFIG:sgi575 = "defconfig"
KCONFIG_MODE:sgi575 = "--alldefconfig"

#
# Total Compute (TC0/TC1) KMACHINE
#
COMPATIBLE_MACHINE:tc = "(tc0|tc1)"
KCONFIG_MODE:tc = "--alldefconfig"
FILESEXTRAPATHS:prepend:tc := "${ARMBSPFILESPATHS}:${THISDIR}/linux-arm64-ack-5.10/tc:"
SRC_URI:append:tc = " \
    file://gki_defconfig \
    file://0001-drm-Add-component-aware-simple-encoder.patch \
    file://0002-drm-arm-komeda-add-RENDER-capability-to-the-device-n.patch \
    file://0003-dt-bindings-mailbox-arm-mhuv2-Add-bindings.patch \
    file://0004-mailbox-arm_mhuv2-Add-driver.patch \
    file://0005-mailbox-arm_mhuv2-Fix-sparse-warnings.patch \
    file://0006-mailbox-arm_mhuv2-make-remove-callback-return-void.patch \
    file://0007-mailbox-arm_mhuv2-Skip-calling-kfree-with-invalid-po.patch \
    file://0008-firmware-arm_ffa-Backport-of-arm_ffa-driver.patch \
    file://0009-tee-add-sec_world_id-to-struct-tee_shm.patch \
    file://0010-optee-simplify-optee_release.patch \
    file://0011-optee-sync-OP-TEE-headers.patch \
    file://0012-optee-refactor-driver-with-internal-callbacks.patch \
    file://0013-optee-add-a-FF-A-memory-pool.patch \
    file://0014-optee-add-FF-A-support.patch \
    file://0015-coresight-etm4x-Save-restore-TRFCR_EL1.patch \
    file://0016-coresight-etm4x-Use-Trace-Filtering-controls-dynamic.patch \
    file://0017-perf-arm-cmn-Use-irq_set_affinity.patch \
    file://0018-perf-arm-cmn-Fix-CPU-hotplug-unregistration.patch \
    file://0019-perf-arm-cmn-Account-for-NUMA-affinity.patch \
    file://0020-perf-arm-cmn-Drop-compile-test-restriction.patch \
    file://0021-perf-arm-cmn-Refactor-node-ID-handling.patch \
    file://0022-perf-arm-cmn-Streamline-node-iteration.patch \
    file://0023-drivers-perf-arm-cmn-Add-space-after.patch \
    file://0024-perf-arm-cmn-Refactor-DTM-handling.patch \
    file://0025-perf-arm-cmn-Optimise-DTM-counter-reads.patch \
    file://0026-perf-arm-cmn-Optimise-DTC-counter-accesses.patch \
    file://0027-perf-arm-cmn-Move-group-validation-data-off-stack.patch \
    file://0028-perf-arm-cmn-Demarcate-CMN-600-specifics.patch \
    file://0029-perf-arm-cmn-Support-new-IP-features.patch \
    file://0030-perf-arm-cmn-Add-CI-700-Support.patch \
    file://0031-firmware-arm_ffa-Fix-uuid-argument-passed-to-ffa_par.patch \
    file://0032-firmware-arm_ffa-Add-ffa_dev_get_drvdata.patch \
    file://0033-firmware-arm_ffa-extern-ffa_bus_type.patch \
    file://0034-firmware-arm_ffa-Fix-FFA_MEM_SHARE-and-FFA_MEM_FRAG_.patch \
    file://0035-ANDROID-trusty-Backport-of-trusty-driver.patch \
    file://0036-ANDROID-trusty-Remove-FFA-specific-initilization.patch \
    file://0037-ANDROID-trusty-Rename-transfer-memory-function-to-le.patch \
    file://0038-ANDROID-trusty-Separate-out-SMC-based-transport.patch \
    file://0039-ANDROID-trusty-Modify-device-compatible-string.patch \
    file://0040-ANDROID-trusty-Add-transport-descriptor.patch \
    file://0041-ANDROID-trusty-Add-trusty-ffa-driver.patch \
    file://0042-ANDROID-trusty-ffa-Add-support-for-FFA-memory-operat.patch \
    file://0043-ANDROID-trusty-ffa-Enable-FFA-transport-for-both-mem.patch \
    file://0044-ANDROID-trusty-Make-trusty-transports-configurable.patch \
    file://init_disassemble_info-signature-changes-causes-compile-failures.patch \
    "
KERNEL_FEATURES:append:tc = " bsp/arm-platforms/tc.scc"
KERNEL_FEATURES:append:tc1 = " bsp/arm-platforms/tc-autofdo.scc"
