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

SRCREV_machine_qemuarm ?= "42cf4d6a1bc84b90681cb82ad95c129387d76b4b"
SRCREV_machine_qemuarm64 ?= "60b649971940737dc7e3a7f247c62ffbd7c82e4c"
SRCREV_machine_qemumips ?= "571315a5526b9e22262cf99bae7c0dd6e5bd204c"
SRCREV_machine_qemuppc ?= "60b649971940737dc7e3a7f247c62ffbd7c82e4c"
SRCREV_machine_qemux86 ?= "60b649971940737dc7e3a7f247c62ffbd7c82e4c"
SRCREV_machine_qemux86-64 ?= "60b649971940737dc7e3a7f247c62ffbd7c82e4c"
SRCREV_machine_qemumips64 ?= "e7889ba18f060368d4ab35e70b076728d73ba622"
SRCREV_machine ?= "60b649971940737dc7e3a7f247c62ffbd7c82e4c"
SRCREV_meta ?= "4f825eeb783a279216ee45ed3b9a63dd6837f7d7"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.12.20"

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
