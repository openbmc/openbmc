SUMMARY = "A Linux kernel module providing user space access to Trusted Services"
DESCRIPTION = "${SUMMARY}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=05e355bbd617507216a836c56cf24983"

inherit module

SRC_URI = "git://gitlab.arm.com/linux-arm/linux-trusted-services;protocol=https;branch=main \
           file://Makefile;subdir=git \
          "
S = "${WORKDIR}/git"

# Tag tee-v1.1.2
SRCREV = "8a81f5d2406f146b15a705d49b256efaa5fa3ba9"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"
KERNEL_MODULE_AUTOLOAD += "arm-ffa-tee"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/uapi/arm_ffa_tee.h ${D}${includedir}/
}
