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

SRCREV_machine_qemuarm ?= "4d2c95e78cdc7d312b7ab231ce90dce317f45df9"
SRCREV_machine_qemuarm64 ?= "2ce56d130ddff67f43ec857cc51cd347666a0078"
SRCREV_machine_qemumips ?= "81454f95166056a253c8950980e025ee243d8074"
SRCREV_machine_qemuppc ?= "42c41e606b70fd73a202f4146c0480f5624b0a0e"
SRCREV_machine_qemux86 ?= "2ce56d130ddff67f43ec857cc51cd347666a0078"
SRCREV_machine_qemux86-64 ?= "2ce56d130ddff67f43ec857cc51cd347666a0078"
SRCREV_machine_qemumips64 ?= "8a481005da41f82d2a40bf8cb40334547160ab5b"
SRCREV_machine ?= "2ce56d130ddff67f43ec857cc51cd347666a0078"
SRCREV_meta ?= "7140ddb86e4b01529185e6d4a606001ad152b8f3"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.38"

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
