KBRANCH ?= "v5.0/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v5.0/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v5.0/standard/qemuarm64"
KBRANCH_qemumips ?= "v5.0/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v5.0/standard/qemuppc"
KBRANCH_qemux86  ?= "v5.0/standard/base"
KBRANCH_qemux86-64 ?= "v5.0/standard/base"
KBRANCH_qemumips64 ?= "v5.0/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "18a5ffa0e90184c8503ff1df45e4e5e501b1bab3"
SRCREV_machine_qemuarm64 ?= "3df4aae6074e94e794e27fe7f17451d9353cdf3d"
SRCREV_machine_qemumips ?= "c4d3cd6437329407e2cc7e3e4615769452d426b1"
SRCREV_machine_qemuppc ?= "3df4aae6074e94e794e27fe7f17451d9353cdf3d"
SRCREV_machine_qemux86 ?= "3df4aae6074e94e794e27fe7f17451d9353cdf3d"
SRCREV_machine_qemux86-64 ?= "3df4aae6074e94e794e27fe7f17451d9353cdf3d"
SRCREV_machine_qemumips64 ?= "2468c20c15477ebe5ecd73c0e0162d7432149318"
SRCREV_machine ?= "3df4aae6074e94e794e27fe7f17451d9353cdf3d"
SRCREV_meta ?= "f0b575cda6d03540ff8da2f70421f13a08f34a5e"

# remap qemuarm to qemuarma15 for the 5.0 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.0;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "5.0.3"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

KERNEL_DEVICETREE_qemuarm = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
