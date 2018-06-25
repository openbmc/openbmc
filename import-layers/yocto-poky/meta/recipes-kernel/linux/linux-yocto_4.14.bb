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

SRCREV_machine_qemuarm ?= "d6268fc3460d3904fd49087f7a822efbaab9bfe8"
SRCREV_machine_qemuarm64 ?= "c94189843b8ad62cafe9a307e7f7d60741690505"
SRCREV_machine_qemumips ?= "4afd92347b2b35dc8e0006712f8fa00ac57f2a36"
SRCREV_machine_qemuppc ?= "e8af5c9b65c5187d148ecd11bd7979489460ca64"
SRCREV_machine_qemux86 ?= "74f6cd2b6976e37491779fcb1bc4966d3a61492c"
SRCREV_machine_qemux86-64 ?= "74f6cd2b6976e37491779fcb1bc4966d3a61492c"
SRCREV_machine_qemumips64 ?= "9863b327e770b42b8c18da3e0cfaf06e8f99ae97"
SRCREV_machine ?= "74f6cd2b6976e37491779fcb1bc4966d3a61492c"
SRCREV_meta ?= "ea9330894eea727bd1655569b16f338976b72563"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.14;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.14.30"

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
