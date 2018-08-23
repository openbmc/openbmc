KBRANCH ?= "standard/preempt-rt/base"

require recipes-kernel/linux/linux-yocto.inc

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME") == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-yocto-rt":
        raise bb.parse.SkipRecipe("Set PREFERRED_PROVIDER_virtual/kernel to linux-yocto-rt to enable it")
}

SRCREV_machine ?= "ef88c3326f62cec4b98340324ddbe7f7f7704fd5"
SRCREV_meta ?= "2ae65226f64ed5c888d60eef76b6249db678d060"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.12.git;branch=${KBRANCH};name=machine \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.12;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.12.28"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

LINUX_KERNEL_TYPE = "preempt-rt"

COMPATIBLE_MACHINE = "(qemux86|qemux86-64|qemuarm|qemuppc|qemumips)"

KERNEL_DEVICETREE_qemuarm = "versatile-pb.dtb"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/taskstats/taskstats.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc"
