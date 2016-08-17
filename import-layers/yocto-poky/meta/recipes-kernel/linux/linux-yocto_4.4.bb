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

SRCREV_machine_qemuarm ?= "ec9167bcb0ccd4897177c5e235af68b5890aa326"
SRCREV_machine_qemuarm64 ?= "1a72cec834de2c80b5563f8afbeea7664fd5ee05"
SRCREV_machine_qemumips ?= "900f98d3ab6498bc7f4ff6b7a489db3bca4e6417"
SRCREV_machine_qemuppc ?= "1a72cec834de2c80b5563f8afbeea7664fd5ee05"
SRCREV_machine_qemux86 ?= "1a72cec834de2c80b5563f8afbeea7664fd5ee05"
SRCREV_machine_qemux86-64 ?= "1a72cec834de2c80b5563f8afbeea7664fd5ee05"
SRCREV_machine_qemumips64 ?= "4b08dfb1fb3bc7659e8f3e13665597d9a9f0816d"
SRCREV_machine ?= "1a72cec834de2c80b5563f8afbeea7664fd5ee05"
SRCREV_meta ?= "bcc65090840f51a6ac937297be5c22fe268d01ab"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.4.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.4;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.4.3"

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
