SUMMARY = "FF-A Debugfs Linux kernel module"
DESCRIPTION = "This out-of-tree kernel module exposes FF-A operations to user space \
used for development purposes"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=05e355bbd617507216a836c56cf24983"

inherit module

SRC_URI = "git://gitlab.arm.com/linux-arm/linux-trusted-services;protocol=https;branch=debugfs \
           file://Makefile;subdir=git \
          "
S = "${WORKDIR}/git"

# Tag debugfs-v5.0.1
SRCREV = "18e3be71f65a405dfb5d97603ae71b3c11759861"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"
KERNEL_MODULE_AUTOLOAD += "arm-ffa-user"
KERNEL_MODULE_PROBECONF += "arm-ffa-user"

# This debugfs driver is used only by uefi-test for testing SmmGW SP
# UUIDs = SMM Gateway SP
FFA-USER-UUID-LIST ?= "ed32d533-99e6-4209-9cc0-2d72cdd998a7"
module_conf_arm-ffa-user = "options arm-ffa-user uuid_str_list=${FFA-USER-UUID-LIST}"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/arm_ffa_user.h ${D}${includedir}/
}
