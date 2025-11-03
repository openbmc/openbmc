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
SRC_URI:append:juno = " ${SRC_URI_KMETA}"
SRC_URI:append:n1sdp = " ${SRC_URI_KMETA}"

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
        "

SRC_URI:append:corstone1000 = " ${@bb.utils.contains('MACHINE_FEATURES', \
                                                            'corstone1000_kernel_debug', \
                                                            'file://corstone1000_kernel_debug.cfg', \
                                                            '', \
                                                             d)}"
SRC_URI:append:corstone1000 = " \
    ${@bb.utils.contains( \
              'MACHINE_FEATURES', \
              'corstone1000-extsys', \
              ' \
		  file://extsys.cfg \
		  file://0001-remoteproc-Add-Arm-remoteproc-driver.patch \
		  file://0002-arm64-dts-Add-corstone1000-external-system-device-no.patch \
		  file://0003-dt-bindings-remoteproc-Add-Arm-remoteproc.patch \
              ', \
              '', \
              d \
        ) \
    } \
"

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
FILESEXTRAPATHS:prepend:fvp-base := "${ARMBSPFILESPATHS}:${ARMFILESPATHS}"
SRC_URI:append:fvp-base = " \
    file://0001-arm64-dts-fvp-Enable-virtio-rng-support.patch \
    file://tee.cfg \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', \
                         'file://no-strict-devmem.cfg', '' , d)} \
"


#
# Juno KMACHINE
#
COMPATIBLE_MACHINE:juno = "juno"
FILESEXTRAPATHS:prepend:juno := "${ARMBSPFILESPATHS}"
SRC_URI:append:juno = " \
    file://no-fsl-ifc-nand.cfg \
    "

#
# Musca B1/S2 can't run Linux
#
COMPATIBLE_MACHINE:musca-b1 = "(^$)"
COMPATIBLE_MACHINE:musca-s1 = "(^$)"

#
# N1SDP KMACHINE
#
FILESEXTRAPATHS:prepend:n1sdp := "${THISDIR}/linux-yocto-6.6/n1sdp:"
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
    file://0006-arm64-defconfig-disable-config-options-that-does-not.patch \
    file://enable-nvme.cfg \
    file://enable-realtek-R8169.cfg \
    file://enable-usb_conn_gpio.cfg \
    file://usb_xhci_pci_renesas.cfg \
    file://no-fsl-ifc-nand.cfg \
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
KBUILD_DEFCONFIG:tc = "defconfig"
KCONFIG_MODE:tc = "--alldefconfig"

#
# sbsa-ref KMACHINE
#
COMPATIBLE_MACHINE:sbsa-ref = "sbsa-ref"
KBUILD_DEFCONFIG:sbsa-ref = "defconfig"
KCONFIG_MODE:sbsa-ref = "--alldefconfig"
