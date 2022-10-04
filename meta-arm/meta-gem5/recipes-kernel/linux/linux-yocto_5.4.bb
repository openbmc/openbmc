KBRANCH ?= "v5.4/standard/base"

require recipes-kernel/linux/linux-yocto.inc

SRCREV_machine ?= "8a59dfded81659402005acfb06fbb00b71c8ce86"
SRCREV_meta ?= "e8c675c7e11fbd96cd812dfb9f4f6fb6f92b6abb"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.4;destsuffix=${KMETA} \
           file://0001-lib-build_OID_registry-fix-reproducibility-issues.patch \
           file://0002-vt-conmakehash-improve-reproducibility.patch \
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "5.4.205"

DEPENDS += "openssl-native util-linux-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

# Functionality flags
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
