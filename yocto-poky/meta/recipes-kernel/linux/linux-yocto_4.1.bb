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

SRCREV_machine_qemuarm ?= "232e2d88841c7e20857350275d80f5585ed93734"
SRCREV_machine_qemuarm64 ?= "91f62d4a0a31919d9b2ccdaf881130f99b61fc5e"
SRCREV_machine_qemumips ?= "7d3690c0762f11e038f14310a50ca1cdc813cc22"
SRCREV_machine_qemuppc ?= "91f62d4a0a31919d9b2ccdaf881130f99b61fc5e"
SRCREV_machine_qemux86 ?= "91f62d4a0a31919d9b2ccdaf881130f99b61fc5e"
SRCREV_machine_qemux86-64 ?= "91f62d4a0a31919d9b2ccdaf881130f99b61fc5e"
SRCREV_machine_qemumips64 ?= "b71e4cd7d45f03048ad31e2a36dfe5a43f021b51"
SRCREV_machine ?= "91f62d4a0a31919d9b2ccdaf881130f99b61fc5e"
SRCREV_meta ?= "a4f88c3fad887e1c559d03ae1b531ca267137b69"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.18"

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
