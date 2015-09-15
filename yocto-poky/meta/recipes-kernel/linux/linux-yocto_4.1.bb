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

SRCREV_machine_qemuarm ?= "3c1245d162ccb55de1af42bcf3dbf690457bf9e4"
SRCREV_machine_qemuarm64 ?= "59b8c4f5e8ddb9c33c62fff22204fe2b0d8c703e"
SRCREV_machine_qemumips ?= "4132a691d0908d10b8f07ce7ece02e6dc94e17bc"
SRCREV_machine_qemuppc ?= "59b8c4f5e8ddb9c33c62fff22204fe2b0d8c703e"
SRCREV_machine_qemux86 ?= "59b8c4f5e8ddb9c33c62fff22204fe2b0d8c703e"
SRCREV_machine_qemux86-64 ?= "59b8c4f5e8ddb9c33c62fff22204fe2b0d8c703e"
SRCREV_machine_qemumips64 ?= "033e1aa633465449edf544eb81adda0caf16ec60"
SRCREV_machine ?= "59b8c4f5e8ddb9c33c62fff22204fe2b0d8c703e"
SRCREV_meta ?= "429f9e2ff0649b8c9341345622545d874d5e303a"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.6"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

COMPATIBLE_MACHINE = "qemuarm|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
