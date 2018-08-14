KBRANCH ?= "v4.15/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v4.15/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v4.15/standard/qemuarm64"
KBRANCH_qemumips ?= "v4.15/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v4.15/standard/qemuppc"
KBRANCH_qemux86  ?= "v4.15/standard/base"
KBRANCH_qemux86-64 ?= "v4.15/standard/base"
KBRANCH_qemumips64 ?= "v4.15/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "4b6902b42f47593928117b2ff0900cd965cf6443"
SRCREV_machine_qemuarm64 ?= "91084d030bc841c483c31e8664289c7940aa5506"
SRCREV_machine_qemumips ?= "19ba2d843750ff65d8fe590acdfc99aea6153945"
SRCREV_machine_qemuppc ?= "91084d030bc841c483c31e8664289c7940aa5506"
SRCREV_machine_qemux86 ?= "91084d030bc841c483c31e8664289c7940aa5506"
SRCREV_machine_qemux86-64 ?= "91084d030bc841c483c31e8664289c7940aa5506"
SRCREV_machine_qemumips64 ?= "97000c3f4664643bac7828bbdc048f7ec216cc31"
SRCREV_machine ?= "91084d030bc841c483c31e8664289c7940aa5506"
SRCREV_meta ?= "939d935b0c992c6f1e51a7a1c9e4fbe6ef3c3174"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.15;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.15.13"

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
