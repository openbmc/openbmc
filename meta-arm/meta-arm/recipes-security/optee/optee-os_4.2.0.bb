require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "12d7c4ee4642d2d761e39fbcf21a06fb77141dea"
SRC_URI += " \
    file://0003-optee-enable-clang-support.patch \
   "
