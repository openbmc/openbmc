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
    file://extsys.cfg \
    file://0001-remoteproc-Add-Arm-remoteproc-driver.patch \
    file://0002-arm64-dts-Add-corstone1000-external-system-device-no.patch \
    file://0003-dt-bindings-remoteproc-Add-Arm-remoteproc.patch \
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

#
# Musca B1/S2 can't run Linux
#
COMPATIBLE_MACHINE:musca-b1 = "(^$)"
COMPATIBLE_MACHINE:musca-s1 = "(^$)"

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
