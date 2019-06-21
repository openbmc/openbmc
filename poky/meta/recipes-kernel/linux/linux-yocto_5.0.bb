KBRANCH ?= "v5.0/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v5.0/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v5.0/standard/qemuarm64"
KBRANCH_qemumips ?= "v5.0/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v5.0/standard/qemuppc"
KBRANCH_qemuriscv64  ?= "v5.0/standard/base"
KBRANCH_qemux86  ?= "v5.0/standard/base"
KBRANCH_qemux86-64 ?= "v5.0/standard/base"
KBRANCH_qemumips64 ?= "v5.0/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "9161b2fa2f1cec0ba02976c389c788445858e0de"
SRCREV_machine_qemuarm64 ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_machine_qemumips ?= "7de9b8f0db98e51a666477c8e2b64f1964b45410"
SRCREV_machine_qemuppc ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_machine_qemuriscv64 ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_machine_qemux86 ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_machine_qemux86-64 ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_machine_qemumips64 ?= "5a8b27bcc0b16077ab8edfcd3fb25c80dc2c652e"
SRCREV_machine ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_meta ?= "31de88e51d100f2c3eefb7acb7390b0144bcfc69"

# remap qemuarm to qemuarma15 for the 5.0 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.0;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "5.0.19"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

KERNEL_DEVICETREE_qemuarmv5 = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64|qemuriscv64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "" ,d)}"
