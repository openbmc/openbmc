require optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os-3.18.0:"

SRCREV = "1ee647035939e073a2e8dddb727c0f019cc035f1"
SRC_URI:append = " \
    file://0004-core-Define-section-attributes-for-clang.patch \
    file://0005-core-ldelf-link-add-z-execstack.patch \
    file://0006-arm32-libutils-libutee-ta-add-.note.GNU-stack-sectio.patch \
   "
