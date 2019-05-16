KBRANCH ?= "v4.19/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v4.19/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v4.19/standard/qemuarm64"
KBRANCH_qemumips ?= "v4.19/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v4.19/standard/qemuppc"
KBRANCH_qemux86  ?= "v4.19/standard/base"
KBRANCH_qemux86-64 ?= "v4.19/standard/base"
KBRANCH_qemumips64 ?= "v4.19/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "b266576aa10e76afeded8e13ffce93c4638b8ddb"
SRCREV_machine_qemuarm64 ?= "1afd139db231e38e8e0f5ea03dcb77f9537ba2e0"
SRCREV_machine_qemumips ?= "f6ef1a5233db0a1f569d7d6638fa803ebe5c867b"
SRCREV_machine_qemuppc ?= "1afd139db231e38e8e0f5ea03dcb77f9537ba2e0"
SRCREV_machine_qemux86 ?= "1afd139db231e38e8e0f5ea03dcb77f9537ba2e0"
SRCREV_machine_qemux86-64 ?= "1afd139db231e38e8e0f5ea03dcb77f9537ba2e0"
SRCREV_machine_qemumips64 ?= "f5b26e4d7a0eee36298d970590c50c26a800b84d"
SRCREV_machine ?= "1afd139db231e38e8e0f5ea03dcb77f9537ba2e0"
SRCREV_meta ?= "9bda6190bfc9e7858c2f7588109a0ec966f37a09"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.19;destsuffix=${KMETA} \
          "

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "4.19.34"

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
