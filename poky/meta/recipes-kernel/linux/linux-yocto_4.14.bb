KBRANCH ?= "v4.14/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v4.14/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v4.14/standard/qemuarm64"
KBRANCH_qemumips ?= "v4.14/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v4.14/standard/qemuppc"
KBRANCH_qemux86  ?= "v4.14/standard/base"
KBRANCH_qemux86-64 ?= "v4.14/standard/base"
KBRANCH_qemumips64 ?= "v4.14/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "93d58c0c59d1dcdba6ff76ef093de7de339414a8"
SRCREV_machine_qemuarm64 ?= "888066bc1b9cc5f596da8237cbf74417106e8f22"
SRCREV_machine_qemumips ?= "a9d862bb92707f39c0cf2b2cc6f1645e88a99eb9"
SRCREV_machine_qemuppc ?= "d8ced31602b65fb92487865502da595bd113a329"
SRCREV_machine_qemux86 ?= "084af9624d268ddf4fd65b2f9e8e50ca2f22e62b"
SRCREV_machine_qemux86-64 ?= "084af9624d268ddf4fd65b2f9e8e50ca2f22e62b"
SRCREV_machine_qemumips64 ?= "44e1719a8f4fe10e88c13b9ec6c1fa1d041efaed"
SRCREV_machine ?= "084af9624d268ddf4fd65b2f9e8e50ca2f22e62b"
SRCREV_meta ?= "c43c9e19a22367b48c0f62764c8555643d2a6844"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.14;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.14.67"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

KERNEL_DEVICETREE_qemuarm = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
