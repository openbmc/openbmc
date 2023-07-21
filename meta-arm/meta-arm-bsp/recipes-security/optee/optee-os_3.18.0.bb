require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${P}:"

SRCREV = "1ee647035939e073a2e8dddb727c0f019cc035f1"
SRC_URI += " \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0002-optee-enable-clang-support.patch \
    file://0003-core-link-add-no-warn-rwx-segments.patch \
    file://0004-core-Define-section-attributes-for-clang.patch \
    file://0005-core-ldelf-link-add-z-execstack.patch \
    file://0006-arm32-libutils-libutee-ta-add-.note.GNU-stack-sectio.patch \
   "
