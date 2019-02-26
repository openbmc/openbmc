KBRANCH ?= "v4.18/standard/tiny/base"
KBRANCH_qemuarm  ?= "v4.15/standard/tiny/arm-versatile-926ejs"

LINUX_KERNEL_TYPE = "tiny"
KCONFIG_MODE = "--allnoconfig"

require recipes-kernel/linux/linux-yocto.inc

LINUX_VERSION ?= "4.18.27"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

SRCREV_machine_qemuarm ?= "8f59516428463985f2a636b024b6c3f1b2178855"
SRCREV_machine ?= "62f0a3acffffd555f68ed97d5e4faade2b28f3c0"
SRCREV_meta ?= "9e348b6f9db185cb60a34d18fd14a18b5def2c31"

PV = "${LINUX_VERSION}+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;branch=${KBRANCH};name=machine \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.18;destsuffix=${KMETA}"

COMPATIBLE_MACHINE = "qemux86|qemux86-64|qemuarm"

# Functionality flags
KERNEL_FEATURES = ""

KERNEL_DEVICETREE_qemuarm = "versatile-pb.dtb"
