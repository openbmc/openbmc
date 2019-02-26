KBRANCH ?= "v4.18/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v4.18/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v4.18/standard/qemuarm64"
KBRANCH_qemumips ?= "v4.18/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v4.18/standard/qemuppc"
KBRANCH_qemux86  ?= "v4.18/standard/base"
KBRANCH_qemux86-64 ?= "v4.18/standard/base"
KBRANCH_qemumips64 ?= "v4.18/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "e66e05d38c1a0eda0db1f34090ecf7155226fa4c"
SRCREV_machine_qemuarm64 ?= "62f0a3acffffd555f68ed97d5e4faade2b28f3c0"
SRCREV_machine_qemumips ?= "5fb9275fdd72ccd5f61cbbce5fef842fbcf3c957"
SRCREV_machine_qemuppc ?= "62f0a3acffffd555f68ed97d5e4faade2b28f3c0"
SRCREV_machine_qemux86 ?= "62f0a3acffffd555f68ed97d5e4faade2b28f3c0"
SRCREV_machine_qemux86-64 ?= "62f0a3acffffd555f68ed97d5e4faade2b28f3c0"
SRCREV_machine_qemumips64 ?= "19cccc092a7fe2337153c65045bded55ae4d5e4b"
SRCREV_machine ?= "62f0a3acffffd555f68ed97d5e4faade2b28f3c0"
SRCREV_meta ?= "9e348b6f9db185cb60a34d18fd14a18b5def2c31"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.18;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "4.18.27"

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
