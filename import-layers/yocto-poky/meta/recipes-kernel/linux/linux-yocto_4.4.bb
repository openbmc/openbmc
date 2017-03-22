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

SRCREV_machine_qemuarm ?= "2859bb3c9e1bb307d9e4e03032fd8a529137552f"
SRCREV_machine_qemuarm64 ?= "35482df5d5ba0807eb8a7c40b554bd657e3f9987"
SRCREV_machine_qemumips ?= "3c4c23020da8a0596a64c8784c6d3a2dfa1ea324"
SRCREV_machine_qemuppc ?= "35482df5d5ba0807eb8a7c40b554bd657e3f9987"
SRCREV_machine_qemux86 ?= "35482df5d5ba0807eb8a7c40b554bd657e3f9987"
SRCREV_machine_qemux86-64 ?= "35482df5d5ba0807eb8a7c40b554bd657e3f9987"
SRCREV_machine_qemumips64 ?= "5d05923ecc402c127b92543ee44b890de5c53a7e"
SRCREV_machine ?= "35482df5d5ba0807eb8a7c40b554bd657e3f9987"
SRCREV_meta ?= "b846fc6436aa5d4c747d620e83dfda969854d10c"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.4.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.4;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.4.36"

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
