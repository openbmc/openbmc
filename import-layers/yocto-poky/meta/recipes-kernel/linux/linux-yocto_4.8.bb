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

SRCREV_machine_qemuarm ?= "f25e3a184bf0ac7b12ec9c98d71439f4ac911974"
SRCREV_machine_qemuarm64 ?= "b9c5f19c82c717b014eab5dc404b9489badbfc8f"
SRCREV_machine_qemumips ?= "79e11192ca2c1acc714214c2125a8c0296c00413"
SRCREV_machine_qemuppc ?= "7a688297cc810a614f0329371d1389e550a98504"
SRCREV_machine_qemux86 ?= "f6329fd2875778192c03e08be02730180cb0dc71"
SRCREV_machine_qemux86-64 ?= "f6329fd2875778192c03e08be02730180cb0dc71"
SRCREV_machine_qemumips64 ?= "d619311dd8ea9ee95d80d937f08fb2c70c1dc50c"
SRCREV_machine ?= "f6329fd2875778192c03e08be02730180cb0dc71"
SRCREV_meta ?= "c84532b6475fd78b878507a481e2c04714341c07"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.8.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.8;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.8.24"

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
