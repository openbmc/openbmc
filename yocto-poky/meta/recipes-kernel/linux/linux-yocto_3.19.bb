KBRANCH ?= "standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "standard/qemuarm64"
KBRANCH_qemumips ?= "standard/mti-malta32"
KBRANCH_qemuppc  ?= "standard/qemuppc"
KBRANCH_qemux86  ?= "standard/common-pc"
KBRANCH_qemux86-64 ?= "standard/common-pc-64/base"
KBRANCH_qemumips64 ?= "standard/mti-malta64"

SRCREV_machine_qemuarm ?= "857048f10bfe7089ca6007e72431f1c098b07115"
SRCREV_machine_qemuarm64 ?= "e152349de59b43b2a75f2c332b44171df461d5a0"
SRCREV_machine_qemumips ?= "cedbbc7b5e72df2e820bb9e7885f12132c5e2fff"
SRCREV_machine_qemuppc ?= "23a83386e10986a63e6cef712a045445499d002b"
SRCREV_machine_qemux86 ?= "1583bf79b946cd5581d84d8c369b819a5ecb94b4"
SRCREV_machine_qemux86-64 ?= "1583bf79b946cd5581d84d8c369b819a5ecb94b4"
SRCREV_machine_qemumips64 ?= "3eb70cea3532e22ab1b6da9864446621229e6616"
SRCREV_machine ?= "151571a39785218a57c3ae3355cd63694890cc8d"
SRCREV_meta ?= "1016714868249d64fc16692fd7679672b1efa17b"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-3.19.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-3.19;destsuffix=${KMETA}"

LINUX_VERSION ?= "3.19.5"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

COMPATIBLE_MACHINE = "qemuarm|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
