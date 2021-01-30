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

SRCREV_machine_qemuarm ?= "14b463d0e5c1e2ff354244420f9bbc7f0ab80533"
SRCREV_machine_qemuarm64 ?= "3c5d210805d61bea8f8a8081e0e3a89ea8a61f3f"
SRCREV_machine_qemumips ?= "8d8d9afb396f154f366f32948fdff3cf4e269841"
SRCREV_machine_qemuppc ?= "3c5d210805d61bea8f8a8081e0e3a89ea8a61f3f"
SRCREV_machine_qemuriscv64 ?= "3c5d210805d61bea8f8a8081e0e3a89ea8a61f3f"
SRCREV_machine_qemux86 ?= "3c5d210805d61bea8f8a8081e0e3a89ea8a61f3f"
SRCREV_machine_qemux86-64 ?= "3c5d210805d61bea8f8a8081e0e3a89ea8a61f3f"
SRCREV_machine_qemumips64 ?= "01a02b058f9a8941032b298b8d25c673526152f4"
SRCREV_machine ?= "3c5d210805d61bea8f8a8081e0e3a89ea8a61f3f"
SRCREV_meta ?= "b976de4f41df1a50dc84839b64fb0ce2c6f9fb21"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.8;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.8.18"

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
