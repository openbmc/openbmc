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

SRCREV_machine_qemuarm ?= "86b02dd23be1e3b3449885b38ed1b876ebec31e8"
SRCREV_machine_qemuarm64 ?= "bd8f931e213614bc5fdc6aeaa132d273caa002af"
SRCREV_machine_qemumips ?= "67b93101c52504fd5077166c70baa296190e6166"
SRCREV_machine_qemuppc ?= "bd8f931e213614bc5fdc6aeaa132d273caa002af"
SRCREV_machine_qemux86 ?= "bd8f931e213614bc5fdc6aeaa132d273caa002af"
SRCREV_machine_qemux86-64 ?= "bd8f931e213614bc5fdc6aeaa132d273caa002af"
SRCREV_machine_qemumips64 ?= "38da8c72733da9619bbbddf14140204631faf488"
SRCREV_machine ?= "bd8f931e213614bc5fdc6aeaa132d273caa002af"
SRCREV_meta ?= "367bd3633d5a661035f90f0b8daa38e97da1a587"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"

DEPENDS += "openssl-native util-linux-native"

LINUX_VERSION ?= "4.12.26"

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
