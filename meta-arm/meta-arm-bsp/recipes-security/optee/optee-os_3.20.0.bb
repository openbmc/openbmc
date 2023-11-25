require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${P}:"

SRCREV = "8e74d47616a20eaa23ca692f4bbbf917a236ed94"
SRC_URI += " \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0002-optee-enable-clang-support.patch \
    file://0003-core-link-add-no-warn-rwx-segments.patch \
    file://0004-core-Define-section-attributes-for-clang.patch \
    file://0005-core-arm-S-EL1-SPMC-boot-ABI-update.patch \
    file://0006-core-ffa-add-TOS_FW_CONFIG-handling.patch \
    file://0007-core-spmc-handle-non-secure-interrupts.patch \
    file://0008-core-spmc-configure-SP-s-NS-interrupt-action-based-o.patch \
    file://CVE-2023-41325.patch \
   "
