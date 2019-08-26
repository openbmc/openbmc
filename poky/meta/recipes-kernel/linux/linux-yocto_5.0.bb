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

SRCREV_machine_qemuarm ?= "d1ed980ad989252d42386c8bc63b2f5f11985ea4"
SRCREV_machine_qemuarm64 ?= "55dd15336b7301b686a0c183f5372b49c1003d03"
SRCREV_machine_qemumips ?= "1520e78195e64f27be46a46a8d6711c8470fb083"
SRCREV_machine_qemuppc ?= "55dd15336b7301b686a0c183f5372b49c1003d03"
SRCREV_machine_qemuriscv64 ?= "00638cdd8f92869a0f89ebe3289fdbd856ba9458"
SRCREV_machine_qemux86 ?= "55dd15336b7301b686a0c183f5372b49c1003d03"
SRCREV_machine_qemux86-64 ?= "55dd15336b7301b686a0c183f5372b49c1003d03"
SRCREV_machine_qemumips64 ?= "9d4105b32cf123a861bc754377d2f2e156278a7e"
SRCREV_machine ?= "55dd15336b7301b686a0c183f5372b49c1003d03"
SRCREV_meta ?= "7f6e97c357746382d4339e7e0463637e715acd4b"

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
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "" ,d)}"
