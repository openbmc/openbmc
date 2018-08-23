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

SRCREV_machine_qemuarm ?= "b84ecefc243a6ed67d8b6020394963de1240a9f0"
SRCREV_machine_qemuarm64 ?= "e562267bae5b518acca880c929fbbdf6be047e0a"
SRCREV_machine_qemumips ?= "15b1ab68f73fa60dd95a74c640e87e05fad1716d"
SRCREV_machine_qemuppc ?= "e562267bae5b518acca880c929fbbdf6be047e0a"
SRCREV_machine_qemux86 ?= "e562267bae5b518acca880c929fbbdf6be047e0a"
SRCREV_machine_qemux86-64 ?= "e562267bae5b518acca880c929fbbdf6be047e0a"
SRCREV_machine_qemumips64 ?= "57a3f72a020fc84f2da5b0b4c5de4cdbc22b3284"
SRCREV_machine ?= "e562267bae5b518acca880c929fbbdf6be047e0a"
SRCREV_meta ?= "2ae65226f64ed5c888d60eef76b6249db678d060"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"

DEPENDS += "openssl-native util-linux-native"

LINUX_VERSION ?= "4.12.28"

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
