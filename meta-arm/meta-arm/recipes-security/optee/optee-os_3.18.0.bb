require optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}-3.18.0:"

SRCREV = "1ee647035939e073a2e8dddb727c0f019cc035f1"
SRC_URI:append = " \
    file://0001-core-Define-section-attributes-for-clang.patch \
    file://0009-add-z-execstack.patch \
    file://0010-add-note-GNU-stack-section.patch \
   "
