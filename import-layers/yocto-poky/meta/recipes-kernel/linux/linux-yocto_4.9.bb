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

SRCREV_machine_qemuarm ?= "23369eb7e07c839fa73a8c1e85aba37a07bf14c1"
SRCREV_machine_qemuarm64 ?= "eb3b2079ea43b451e06be443f8bc146736f9c4bc"
SRCREV_machine_qemumips ?= "cab9e059447878f5383f91a05db12813f69cbfc1"
SRCREV_machine_qemuppc ?= "eb3b2079ea43b451e06be443f8bc146736f9c4bc"
SRCREV_machine_qemux86 ?= "eb3b2079ea43b451e06be443f8bc146736f9c4bc"
SRCREV_machine_qemux86-64 ?= "eb3b2079ea43b451e06be443f8bc146736f9c4bc"
SRCREV_machine_qemumips64 ?= "c2e5ef83b612d50f50fafeed9930dbea302fbe8c"
SRCREV_machine ?= "eb3b2079ea43b451e06be443f8bc146736f9c4bc"
SRCREV_meta ?= "0774eacea2a7d3a150594533b8c80d0c0bfdfded"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.9.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.9;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.9.82"

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
