COMPATIBLE_MACHINE = "fvp-baser-aemv8r64"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/${MACHINE}:"
SRC_URI:append = " \
    file://0001-aarch64-Rename-labels-and-prepare-for-lower-EL-booti.patch \
    file://0002-aarch64-Prepare-for-EL1-booting.patch \
    file://0003-aarch64-Prepare-for-lower-EL-booting.patch \
    file://0004-gic-v3-Prepare-for-gicv3-with-EL2.patch \
    file://0005-aarch64-Prepare-for-booting-with-EL2.patch \
    file://0006-aarch64-Introduce-EL2-boot-code-for-Armv8-R-AArch64.patch \
    file://0007-Allow-enable-psci-to-choose-between-smc-and-hvc.patch \
    file://0008-aarch64-Disable-CNTPCT_EL0-trap-for-v8-R64.patch \
    file://0009-lds-Mark-the-mem-range.patch \
    file://0010-common-Introduce-the-libfdt.patch \
    file://0011-common-Add-essential-libc-functions.patch \
    file://0012-Makefile-Add-the-libfdt-to-the-Makefile-system.patch \
    file://0013-platform-Add-print_hex-func.patch \
    file://0014-common-Add-mem-usage-to-memreserve.patch \
    file://0015-boot-Add-the-enable-keep-el-compile-option.patch \
    file://0016-Makefile-Change-COUNTER_FREQ-to-100-MHz.patch \
    file://0017-PSCI-Apply-flush-cache-after-setting-branch_data.patch \
    file://0018-PSCI-Add-function-call-entry-point.patch \
    file://0019-lds-Rearrange-and-mark-the-sections.patch \
    file://0020-common-Provide-firmware-info-using-libfdt.patch \
    file://0021-boot-Enable-firmware-node-initialization.patch \
    "

BOOT_WRAPPER_AARCH64_CMDLINE = "\
earlycon console=ttyAMA0 loglevel=8 rootfstype=ext4 root=/dev/vda1 rw"

EXTRA_OECONF += "--enable-psci=hvc --enable-keep-el"

TUNE_CCARGS = ""

BOOT_WRAPPER_AARCH64_KERNEL = "u-boot.bin"
do_deploy[depends] += "u-boot:do_deploy"
