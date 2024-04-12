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

KBRANCH = "v6.9/standard/base"
KMETA = "kernel-meta"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-dev.git;branch=${KBRANCH};name=machine;protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=master;destsuffix=${KMETA};protocol=https"

# Set default SRCREVs. Both the machine and meta SRCREVs are statically set
# to the korg v3.7 tag, and hence prevent network access during parsing. If
# linux-yocto-dev is the preferred provider, they will be overridden to
# AUTOREV in following anonymous python routine and resolved when the
# variables are finalized.
SRCREV_machine ?= '${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-yocto-dev", "${AUTOREV}", "29594404d7fe73cd80eaa4ee8c43dcc53970c60e", d)}'
SRCREV_meta ?= '${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-yocto-dev", "${AUTOREV}", "29594404d7fe73cd80eaa4ee8c43dcc53970c60e", d)}'

LINUX_VERSION ?= "6.9"
LINUX_VERSION_EXTENSION ?= "-yoctodev-${LINUX_KERNEL_TYPE}"
PV = "${LINUX_VERSION}+git"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

# yaml and dtschema are required for 5.16+ device tree validation, libyaml is checked
# via pkgconfig, so must always be present, but we can wrap the others to make them
# conditional
DEPENDS += "libyaml-native"

PACKAGECONFIG ??= ""
PACKAGECONFIG[dt-validation] = ",,python3-dtschema-native"
# we need the wrappers if validation isn't in the packageconfig
DEPENDS += "${@bb.utils.contains('PACKAGECONFIG', 'dt-validation', '', 'python3-dtschema-wrapper-native', d)}"

COMPATIBLE_MACHINE = "^(qemuarmv5|qemuarm|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64|qemuriscv32|qemuriscv64|qemuloongarch64)$"

KERNEL_DEVICETREE:qemuarmv5 = "arm/versatile-pb.dtb"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/taskstats/taskstats.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append:qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc"
KERNEL_FEATURES:append:qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append:qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc features/gpio/sim.scc", "", d)}"

KERNEL_VERSION_SANITY_SKIP = "1"
