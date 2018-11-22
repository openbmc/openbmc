KBRANCH ?= "v4.14/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v4.14/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v4.14/standard/qemuarm64"
KBRANCH_qemumips ?= "v4.14/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v4.14/standard/qemuppc"
KBRANCH_qemux86  ?= "v4.14/standard/base"
KBRANCH_qemux86-64 ?= "v4.14/standard/base"
KBRANCH_qemumips64 ?= "v4.14/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "8752b8421efe8b5a478f17fbffacf4af974ec703"
SRCREV_machine_qemuarm64 ?= "ac66474ba7f7e93d16ae3ea005f214113bb127c5"
SRCREV_machine_qemumips ?= "ab031b267e2a79fcd48da5d10d503f4d065f4821"
SRCREV_machine_qemuppc ?= "f47c3945e8dd230ea37771bcacc836245fc79d22"
SRCREV_machine_qemux86 ?= "f1d93b219bde37a8a286cd18d6af2dcf0d02c1a8"
SRCREV_machine_qemux86-64 ?= "f1d93b219bde37a8a286cd18d6af2dcf0d02c1a8"
SRCREV_machine_qemumips64 ?= "8063a7258fc670a361fed85b858fabb237485f1c"
SRCREV_machine ?= "f1d93b219bde37a8a286cd18d6af2dcf0d02c1a8"
SRCREV_meta ?= "6a3254e7b370cbb86c1f73379dcf38885c1c69e0"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.14;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.14.79"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

KERNEL_DEVICETREE_qemuarm = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
