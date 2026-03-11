require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# v4.6.0
SRCREV = "71785645fa6ce42db40dbf5a54e0eaedc4f61591"
SRC_URI += " \
    file://0001-optee-enable-clang-support.patch \
    file://0002-Add-optee-ta-instanceKeepCrashed.patch \
   "
