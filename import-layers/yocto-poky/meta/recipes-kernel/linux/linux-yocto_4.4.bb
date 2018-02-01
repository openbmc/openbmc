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

SRCREV_machine_qemuarm ?= "840e2c02dde98ee9c584cacdd5bb0812e9dcd016"
SRCREV_machine_qemuarm64 ?= "b71c7b786aed26c0a1e4eca66f1d874ec017d699"
SRCREV_machine_qemumips ?= "27a547fe77b05ae63c3b973a029c3933ef62c164"
SRCREV_machine_qemuppc ?= "b71c7b786aed26c0a1e4eca66f1d874ec017d699"
SRCREV_machine_qemux86 ?= "b71c7b786aed26c0a1e4eca66f1d874ec017d699"
SRCREV_machine_qemux86-64 ?= "b71c7b786aed26c0a1e4eca66f1d874ec017d699"
SRCREV_machine_qemumips64 ?= "7e333c223b568704cc3303b2e922ff85a2a8f7ef"
SRCREV_machine ?= "b71c7b786aed26c0a1e4eca66f1d874ec017d699"
SRCREV_meta ?= "804d2b3164ec25ed519fd695de9aa0908460c92e"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.4.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.4;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.4.87"

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
