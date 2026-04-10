require recipes-kernel/linux/linux-yocto.inc

# provide this .inc to set specific revisions
include recipes-kernel/linux/linux-yocto-dev-revisions.inc

KBRANCH = "v6.19/standard/base"
KMETA = "kernel-meta"

DEFAULT_PREFERENCE = "-1"

# CVE exclusions
include recipes-kernel/linux/cve-exclusion.inc
include recipes-kernel/linux/cve-exclusion_6.19.inc

# linux-yocto-dev pinned to 6.19
KBRANCH = "v6.19/standard/base"
SRCREV_machine = "c042e9e14dd7efb27e9556c42db39425a2860572"
SRCREV_meta = "6df48aa0b0d32d05614d7eda898f8d930a7600b7"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-dev.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=master;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

KCONF_BSP_AUDIT_LEVEL = "1"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc features/nf_tables/nft_test.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc features/gpio/sim.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("KERNEL_DEBUG", "True", " features/reproducibility/reproducibility.scc features/debug/debug-btf.scc", "", d)}"
# libteam ptests from meta-oe needs it
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/net/team/team.scc", "", d)}"
# openl2tp tests from meta-networking needs it
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " cgl/cfg/net/l2tp.scc", "", d)}"
