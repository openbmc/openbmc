require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "18b424c23aa5a798dfe2e4d20b4bde3919dc4e99"
SRC_URI += " \
    file://0003-optee-enable-clang-support.patch \
   "
