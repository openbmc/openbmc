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

# Tag debugfs-v5.0.2
SRCREV = "885af0d1c3bf11aba12320d5484429160a9b9f26"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"
KERNEL_MODULE_AUTOLOAD += "arm-ffa-user"
KERNEL_MODULE_PROBECONF += "arm-ffa-user"

# SMM Gateway SP
UUID_LIST = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', \
                                  'ed32d533-99e6-4209-9cc0-2d72cdd998a7', '' , d)}"
# SPMC Tests SPs
UUID_LIST:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                  ',5c9edbc3-7b3a-4367-9f83-7c191ae86a37,7817164c-c40c-4d1a-867a-9bb2278cf41a,23eb0100-e32a-4497-9052-2f11e584afa6', '' , d)}"

FFA_USER_UUID_LIST ?= "${@d.getVar('UUID_LIST').strip(',')}"

module_conf_arm-ffa-user = "options arm-ffa-user uuid_str_list=${FFA_USER_UUID_LIST}"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/arm_ffa_user.h ${D}${includedir}/
}
