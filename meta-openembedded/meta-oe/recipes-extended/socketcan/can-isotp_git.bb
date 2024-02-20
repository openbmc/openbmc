LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=72d977d697c3c05830fdff00a7448931"
SRCREV = "7626d0a0707391970080d493ce69638719938da7"
PV = "1.0+git"

SRC_URI = "git://github.com/hartkopp/can-isotp.git;protocol=https;branch=master"


S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

do_install:append() {
    install -Dm 644 ${S}/include/uapi/linux/can/isotp.h ${D}${includedir}/linux/can/isotp.h
}

SKIP_RECIPE[can-isotp] ?= "Not needed with kernel 5.10+"
