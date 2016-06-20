# This recipe tracks the linux-yocto-dev repository as its upstream source.
# Since this tree is frequently updated, and periodically rebuilt, AUTOREV is
# used to track its contents.
#
# This recipe is just like other linux-yocto variants, with the only difference
# being that to avoid network access during initial parsing, static SRCREVs are
# provided and overridden if the preferred kernel provider is linux-yocto-dev.
#
# To enable this recipe, set PREFERRED_PROVIDER_virtual/kernel = "linux-yocto-dev"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

# provide this .inc to set specific revisions
include recipes-kernel/linux/linux-yocto-dev-revisions.inc

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids network access required
# by the use of AUTOREV SRCREVs, which are the default for this recipe.
python () {
    if d.getVar("PREFERRED_PROVIDER_virtual/kernel", True) != "linux-yocto-dev":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-yocto-dev to enable it")
}

KBRANCH = "standard/base"
KMETA = "kernel-meta"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-dev.git;branch=${KBRANCH};name=machine \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=master;destsuffix=${KMETA}"

# Set default SRCREVs. Both the machine and meta SRCREVs are statically set
# to the korg v3.7 tag, and hence prevent network access during parsing. If
# linux-yocto-dev is the preferred provider, they will be overridden to
# AUTOREV in following anonymous python routine and resolved when the
# variables are finalized.
SRCREV_machine ?= '${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-yocto-dev", "${AUTOREV}", "29594404d7fe73cd80eaa4ee8c43dcc53970c60e", d)}'
SRCREV_meta ?= '${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-yocto-dev", "${AUTOREV}", "29594404d7fe73cd80eaa4ee8c43dcc53970c60e", d)}'

LINUX_VERSION ?= "4.6-rc+"
LINUX_VERSION_EXTENSION ?= "-yoctodev-${LINUX_KERNEL_TYPE}"
PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE = "(qemuarm|qemux86|qemuppc|qemumips|qemumips64|qemux86-64)"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/taskstats/taskstats.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"

