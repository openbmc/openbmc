COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64-secureboot"
COMPATIBLE_MACHINE:qemu-generic-arm64 = "qemu-generic-arm64"
COMPATIBLE_MACHINE:qemuarm-secureboot = "qemuarm-secureboot"

#FIXME - clang fails to build tfa for qemuarm-secureboot, and possibly other
# arm/aarch32.  This is a known testing hole in TF-A.
TOOLCHAIN:qemuarm-secureboot = "gcc"

TFA_PLATFORM:qemuarm64-secureboot = "qemu"
TFA_PLATFORM:qemu-generic-arm64 = "qemu_sbsa"
TFA_PLATFORM:qemuarm-secureboot = "qemu"

TFA_SPD:qemuarm64-secureboot = "opteed"

TFA_UBOOT:qemuarm64-secureboot = "1"
TFA_UBOOT:qemuarm-secureboot = "1"
TFA_BUILD_TARGET:aarch64:qemuall = "all fip"
TFA_BUILD_TARGET:arm:qemuall = "all fip"

TFA_INSTALL_TARGET:qemuarm64-secureboot = "flash.bin"
TFA_INSTALL_TARGET:qemu-generic-arm64 = "bl1 fip"
TFA_INSTALL_TARGET:qemuarm-secureboot = "flash.bin"

DEPENDS:append:aarch64:qemuall = " optee-os"
DEPENDS:append:arm:qemuall = " optee-os"

EXTRA_OEMAKE:append:aarch64:qemuall = " \
    BL32=${STAGING_DIR_TARGET}${nonarch_base_libdir}/firmware/tee-header_v2.bin \
    BL32_EXTRA1=${STAGING_DIR_TARGET}${nonarch_base_libdir}/firmware/tee-pager_v2.bin \
    BL32_EXTRA2=${STAGING_DIR_TARGET}${nonarch_base_libdir}/firmware/tee-pageable_v2.bin \
    BL32_RAM_LOCATION=tdram \
    "

EXTRA_OEMAKE:append:arm:qemuall = " \
    BL32=${STAGING_DIR_TARGET}${nonarch_base_libdir}/firmware/tee-header_v2.bin \
    BL32_EXTRA1=${STAGING_DIR_TARGET}${nonarch_base_libdir}/firmware/tee-pager_v2.bin \
    BL32_EXTRA2=${STAGING_DIR_TARGET}${nonarch_base_libdir}/firmware/tee-pageable_v2.bin \
    ARM_ARCH_MAJOR=7 \
    ARCH=aarch32 \
    BL32_RAM_LOCATION=tdram \
    AARCH32_SP=optee \
    "

do_compile:append:qemuarm64-secureboot() {
    # Create a secure flash image for booting AArch64 Qemu. See:
    # https://git.trustedfirmware.org/TF-A/trusted-firmware-a.git/tree/docs/plat/qemu.rst
    dd if=${BUILD_DIR}/bl1.bin of=${BUILD_DIR}/flash.bin bs=4096 conv=notrunc
    dd if=${BUILD_DIR}/fip.bin of=${BUILD_DIR}/flash.bin seek=64 bs=4096 conv=notrunc
}

do_compile:append:qemuarm-secureboot() {
    # Create a secure flash image for booting AArch64 Qemu. See:
    # https://git.trustedfirmware.org/TF-A/trusted-firmware-a.git/tree/docs/plat/qemu.rst
    dd if=${BUILD_DIR}/bl1.bin of=${BUILD_DIR}/flash.bin bs=4096 conv=notrunc
    dd if=${BUILD_DIR}/fip.bin of=${BUILD_DIR}/flash.bin seek=64 bs=4096 conv=notrunc
}
