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

SRCREV_machine_qemuarm ?= "45824c60ca37f414a5ac5783e970338db9a5a2af"
SRCREV_machine_qemuarm64 ?= "f9d67777b07ac97966186c1b56db78afe2a16f92"
SRCREV_machine_qemumips ?= "66f741b0b3d093e6b6df0f44120913ef3a259e23"
SRCREV_machine_qemuppc ?= "f9d67777b07ac97966186c1b56db78afe2a16f92"
SRCREV_machine_qemux86 ?= "f9d67777b07ac97966186c1b56db78afe2a16f92"
SRCREV_machine_qemux86-64 ?= "f9d67777b07ac97966186c1b56db78afe2a16f92"
SRCREV_machine_qemumips64 ?= "c5d838c9e26bd657b49dfe28b115e5bc4b580850"
SRCREV_machine ?= "f9d67777b07ac97966186c1b56db78afe2a16f92"
SRCREV_meta ?= "46171de19220c49d670544017cfbeffc1ec70e80"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"

DEPENDS += "openssl-native util-linux-native"

LINUX_VERSION ?= "4.12.24"

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
