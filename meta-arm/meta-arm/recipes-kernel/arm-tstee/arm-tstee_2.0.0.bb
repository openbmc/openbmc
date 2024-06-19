SUMMARY = "A Linux kernel module providing user space access to Trusted Services"
DESCRIPTION = "${SUMMARY}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=05e355bbd617507216a836c56cf24983"

inherit module

SRC_URI = "git://gitlab.arm.com/linux-arm/linux-trusted-services;protocol=https;branch=main \
           file://Makefile;subdir=git \
          "
S = "${WORKDIR}/git"

# Tag tee-v2.0.0
SRCREV = "a2d7349a96c3b3afb44bf1555d53f1c46e45a23d"
UPSTREAM_CHECK_GITTAGREGEX = "^tee-v(?P<pver>\d+(\.\d+)+)$"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"
KERNEL_MODULE_AUTOLOAD += "arm-tstee"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/uapi/arm_tstee.h ${D}${includedir}/
}
