KBRANCH ?= "v5.8/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v5.8/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v5.8/standard/qemuarm64"
KBRANCH_qemumips ?= "v5.8/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v5.8/standard/qemuppc"
KBRANCH_qemuriscv64  ?= "v5.8/standard/base"
KBRANCH_qemux86  ?= "v5.8/standard/base"
KBRANCH_qemux86-64 ?= "v5.8/standard/base"
KBRANCH_qemumips64 ?= "v5.8/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "d351bf87c9c0e96a1f27f87f16d298bc4470e0b5"
SRCREV_machine_qemuarm64 ?= "31fafe701e2adec65d2b2a74a3e592a358915c67"
SRCREV_machine_qemumips ?= "93d29a70890b19fb5482ebcab5f3a49301851daf"
SRCREV_machine_qemuppc ?= "31fafe701e2adec65d2b2a74a3e592a358915c67"
SRCREV_machine_qemuriscv64 ?= "31fafe701e2adec65d2b2a74a3e592a358915c67"
SRCREV_machine_qemux86 ?= "31fafe701e2adec65d2b2a74a3e592a358915c67"
SRCREV_machine_qemux86-64 ?= "31fafe701e2adec65d2b2a74a3e592a358915c67"
SRCREV_machine_qemumips64 ?= "4faa049b6b7b51c5d12d20c5e9fcf8e0a3ba8d42"
SRCREV_machine ?= "31fafe701e2adec65d2b2a74a3e592a358915c67"
SRCREV_meta ?= "ffbfe61a194537689c782b20da185e7e4daa9ef9"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.8;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.8.9"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"
DEPENDS += "gmp-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE_qemuarmv5 = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64|qemuriscv64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "", d)}"
