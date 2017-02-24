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

SRCREV_machine_qemuarm ?= "187bcc13f3023c3ae0a3ba5c69ae85c4e5e693ac"
SRCREV_machine_qemuarm64 ?= "ca6a08bd7f86ebef11f763d26f787f7d65270473"
SRCREV_machine_qemumips ?= "2f273556495dd2871f08c73fc3f40d1ad546c638"
SRCREV_machine_qemuppc ?= "ca6a08bd7f86ebef11f763d26f787f7d65270473"
SRCREV_machine_qemux86 ?= "ca6a08bd7f86ebef11f763d26f787f7d65270473"
SRCREV_machine_qemux86-64 ?= "ca6a08bd7f86ebef11f763d26f787f7d65270473"
SRCREV_machine_qemumips64 ?= "0a19cacf5738876666a4b530a9fa14f05b355299"
SRCREV_machine ?= "ca6a08bd7f86ebef11f763d26f787f7d65270473"
SRCREV_meta ?= "3030330b066a33ce21164a8b30d0503cf9f68e5b"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.4.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.4;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.4.26"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

COMPATIBLE_MACHINE = "qemuarm|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
