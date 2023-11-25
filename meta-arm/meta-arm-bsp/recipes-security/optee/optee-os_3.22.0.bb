require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${P}:"

SRCREV = "001ace6655dd6bb9cbe31aa31b4ba69746e1a1d9"
SRC_URI += " \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0002-core-Define-section-attributes-for-clang.patch \
    file://0003-optee-enable-clang-support.patch \
    file://0004-core-link-add-no-warn-rwx-segments.patch \
   "
