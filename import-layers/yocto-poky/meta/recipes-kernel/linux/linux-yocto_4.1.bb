KBRANCH ?= "standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "standard/qemuarm64"
KBRANCH_qemumips ?= "standard/mti-malta32"
KBRANCH_qemuppc  ?= "standard/qemuppc"
KBRANCH_qemux86  ?= "standard/base"
KBRANCH_qemux86-64 ?= "standard/base"
KBRANCH_qemumips64 ?= "standard/mti-malta64"

SRCREV_machine_qemuarm ?= "d67ef485ce1420df11bda2d9f6fb78ef50c1adff"
SRCREV_machine_qemuarm64 ?= "f4d0900b2851e829e990e0f64b09ed3b8e355fae"
SRCREV_machine_qemumips ?= "65116339cfd210990c9c4710cdfec3ebd59abb0e"
SRCREV_machine_qemuppc ?= "30816907653b57f1f3d5f9a7a2f6339bab14a680"
SRCREV_machine_qemux86 ?= "f4d0900b2851e829e990e0f64b09ed3b8e355fae"
SRCREV_machine_qemux86-64 ?= "f4d0900b2851e829e990e0f64b09ed3b8e355fae"
SRCREV_machine_qemumips64 ?= "f7a0b532b6ac81757d85b0c9a928f45a87c9e364"
SRCREV_machine ?= "f4d0900b2851e829e990e0f64b09ed3b8e355fae"
SRCREV_meta ?= "3c3197e65b6f2f5514853c1fe78ae8ffc131b02c"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.33"

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
