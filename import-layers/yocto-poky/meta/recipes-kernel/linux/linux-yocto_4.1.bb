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

SRCREV_machine_qemuarm ?= "642e9b95f97f8e00ef819bbfb2f281c1079bfbb1"
SRCREV_machine_qemuarm64 ?= "782133d8166ac71ef1ffaba58b7cf81ec9e532a1"
SRCREV_machine_qemumips ?= "54d4575d0df1805e127c5fa59201cd978849fb3f"
SRCREV_machine_qemuppc ?= "58906f2737c644e67a7c9754b7e36630005a0011"
SRCREV_machine_qemux86 ?= "782133d8166ac71ef1ffaba58b7cf81ec9e532a1"
SRCREV_machine_qemux86-64 ?= "782133d8166ac71ef1ffaba58b7cf81ec9e532a1"
SRCREV_machine_qemumips64 ?= "03f0c0293abfe5226c2fdd22328476854fa0127f"
SRCREV_machine ?= "782133d8166ac71ef1ffaba58b7cf81ec9e532a1"
SRCREV_meta ?= "9f9c9a66ef3452343586adf150137967e955d71a"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.43"

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
