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

SRCREV_machine_qemuarm ?= "cf760f381c5e1e58d0c3372d66f4dfdc33f0984c"
SRCREV_machine_qemuarm64 ?= "788dfc9859321c09f1c58696bf8998f90ccb4f51"
SRCREV_machine_qemumips ?= "aa46295ab927bd5c960930c377855dbc4e57b195"
SRCREV_machine_qemuppc ?= "788dfc9859321c09f1c58696bf8998f90ccb4f51"
SRCREV_machine_qemux86 ?= "2e0ac7b6c4e3ada23a84756287e9b7051ace939a"
SRCREV_machine_qemux86-64 ?= "2e0ac7b6c4e3ada23a84756287e9b7051ace939a"
SRCREV_machine_qemumips64 ?= "949c0f2cbb4cf902478d009a7d38b6e4fb29e7c4"
SRCREV_machine ?= "788dfc9859321c09f1c58696bf8998f90ccb4f51"
SRCREV_meta ?= "46bb64d605fd336d99fa05bab566b9553b40b4b4"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.15"
LINUX_VERSION_qemux86 ?= "4.1.17"
LINUX_VERSION_qemux86-64 ?= "4.1.17"

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
