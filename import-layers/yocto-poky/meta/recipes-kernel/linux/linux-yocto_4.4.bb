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

SRCREV_machine_qemuarm ?= "7d2a3c70d62f1e7f4eba571c49ff299db2bb3829"
SRCREV_machine_qemuarm64 ?= "0298d3765a5c474ff5776284d49111276510d4b4"
SRCREV_machine_qemumips ?= "6100965a51cf6b99f57cf8234aa982beb79455c9"
SRCREV_machine_qemuppc ?= "0298d3765a5c474ff5776284d49111276510d4b4"
SRCREV_machine_qemux86 ?= "0298d3765a5c474ff5776284d49111276510d4b4"
SRCREV_machine_qemux86-64 ?= "0298d3765a5c474ff5776284d49111276510d4b4"
SRCREV_machine_qemumips64 ?= "522e709fd7088e1a55e7a4708b1a07caa2ca4336"
SRCREV_machine ?= "0298d3765a5c474ff5776284d49111276510d4b4"
SRCREV_meta ?= "d6733af2080f8c0775569adc0826eb0c8954fc5e"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.4.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.4;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.4.60"

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
