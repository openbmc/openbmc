require optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os-3.20.0:"

SRCREV = "8e74d47616a20eaa23ca692f4bbbf917a236ed94"
SRC_URI:append = " \
    file://0004-core-Define-section-attributes-for-clang.patch \
   "
