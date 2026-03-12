# Machine specific TFAs

QEMU_TFA_REQUIRE ?= ""
QEMU_TFA_REQUIRE:qemuarm-secureboot = "trusted-firmware-a-qemuarm-secureboot.inc"
QEMU_TFA_REQUIRE:qemuarm64-secureboot = "trusted-firmware-a-qemuarm64-secureboot.inc"

require ${QEMU_TFA_REQUIRE}

TFA_BUILD_TARGET:aarch64:qemuall = "all fip"
TFA_BUILD_TARGET:arm:qemuall = "all fip"

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
