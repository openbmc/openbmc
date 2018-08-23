KBRANCH ?= "standard/tiny/common-pc"
LINUX_KERNEL_TYPE = "tiny"
KCONFIG_MODE = "--allnoconfig"

require recipes-kernel/linux/linux-yocto.inc

LINUX_VERSION ?= "4.12.28"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

SRCREV_machine ?= "e562267bae5b518acca880c929fbbdf6be047e0a"
SRCREV_meta ?= "2ae65226f64ed5c888d60eef76b6249db678d060"

PV = "${LINUX_VERSION}+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;branch=${KBRANCH};name=machine \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"

COMPATIBLE_MACHINE = "qemux86|qemux86-64"

# Functionality flags
KERNEL_FEATURES = ""

KERNEL_DEVICETREE_qemuarm = "versatile-pb.dtb"
