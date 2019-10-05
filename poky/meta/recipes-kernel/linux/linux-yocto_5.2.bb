KBRANCH ?= "v5.2/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v5.2/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v5.2/standard/qemuarm64"
KBRANCH_qemumips ?= "v5.2/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v5.2/standard/qemuppc"
KBRANCH_qemuriscv64  ?= "v5.2/standard/base"
KBRANCH_qemux86  ?= "v5.2/standard/base"
KBRANCH_qemux86-64 ?= "v5.2/standard/base"
KBRANCH_qemumips64 ?= "v5.2/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "d956bb318771761069228fc70fea722ceabecb75"
SRCREV_machine_qemuarm64 ?= "25b14cdf9659c68ab32d66616dfdbbe2ece3fd94"
SRCREV_machine_qemumips ?= "9e95334637a4a85b19bea120f9027ff0028d7069"
SRCREV_machine_qemuppc ?= "25b14cdf9659c68ab32d66616dfdbbe2ece3fd94"
SRCREV_machine_qemuriscv64 ?= "25b14cdf9659c68ab32d66616dfdbbe2ece3fd94"
SRCREV_machine_qemux86 ?= "25b14cdf9659c68ab32d66616dfdbbe2ece3fd94"
SRCREV_machine_qemux86-64 ?= "25b14cdf9659c68ab32d66616dfdbbe2ece3fd94"
SRCREV_machine_qemumips64 ?= "29a8d126f60d1b9e9cf0985b38da2033ebde25db"
SRCREV_machine ?= "25b14cdf9659c68ab32d66616dfdbbe2ece3fd94"
SRCREV_meta ?= "56f5bc749e5a8e40b55a3819288ac277813ec5ef"

# remap qemuarm to qemuarma15 for the 5.2 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.2;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "5.2.17"

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
