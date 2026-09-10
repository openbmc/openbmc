SUMMARY = "devtool test for overrides and patches"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
INHIBIT_DEFAULT_DEPS = "1"
EXCLUDE_FROM_WORLD = "1"

SRC_URI = "file://source;subdir=${BP}"
SRC_URI:append:qemuarm = " file://arm.patch;striplevel=0"
SRC_URI:append:qemux86 = " file://x86.patch;striplevel=0"
