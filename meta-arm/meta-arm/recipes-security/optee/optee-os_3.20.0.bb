require optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os-3.20.0:"

SRCREV = "8e74d47616a20eaa23ca692f4bbbf917a236ed94"
SRC_URI:append = " \
    file://0004-core-Define-section-attributes-for-clang.patch \
    file://0005-core-arm-S-EL1-SPMC-boot-ABI-update.patch \
    file://0006-core-ffa-add-TOS_FW_CONFIG-handling.patch \
    file://0007-core-spmc-handle-non-secure-interrupts.patch \
    file://0008-core-spmc-configure-SP-s-NS-interrupt-action-based-o.patch \
   "
