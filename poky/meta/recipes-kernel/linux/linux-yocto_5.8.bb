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

SRCREV_machine_qemuarm ?= "097417e785af04be0cbe757bc6e24456a3f701fd"
SRCREV_machine_qemuarm64 ?= "d3c69e89ee5b5d4c3c19b8614bdcdc3f5dc7a8b3"
SRCREV_machine_qemumips ?= "1fc5490bef8322680d73f6ab2c7b666eccc3bce1"
SRCREV_machine_qemuppc ?= "d3c69e89ee5b5d4c3c19b8614bdcdc3f5dc7a8b3"
SRCREV_machine_qemuriscv64 ?= "d3c69e89ee5b5d4c3c19b8614bdcdc3f5dc7a8b3"
SRCREV_machine_qemux86 ?= "d3c69e89ee5b5d4c3c19b8614bdcdc3f5dc7a8b3"
SRCREV_machine_qemux86-64 ?= "d3c69e89ee5b5d4c3c19b8614bdcdc3f5dc7a8b3"
SRCREV_machine_qemumips64 ?= "e61fc06792254eed92c6908a9b35790ed54b0ace"
SRCREV_machine ?= "d3c69e89ee5b5d4c3c19b8614bdcdc3f5dc7a8b3"
SRCREV_meta ?= "a3138cb23c3b7409c516d5d2115da9534c120a0c"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.8;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.8.1"

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
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "" ,d)}"
