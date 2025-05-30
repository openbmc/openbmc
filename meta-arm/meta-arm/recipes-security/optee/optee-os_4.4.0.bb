require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# v4.4.0
SRCREV = "8f645256efc0dc66bd5c118778b0b50c44469ae1"
SRC_URI += " \
    file://0003-optee-enable-clang-support.patch \
   "
