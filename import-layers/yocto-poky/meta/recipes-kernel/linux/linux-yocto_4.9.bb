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

SRCREV_machine_qemuarm ?= "8caa35a74753d45178720933f03d8d5150a8ff17"
SRCREV_machine_qemuarm64 ?= "480ee599fb8df712c10dcf4b7aa6398b79f7d404"
SRCREV_machine_qemumips ?= "fc2a3b9f932779fdf053675a5a73e8f9917507a5"
SRCREV_machine_qemuppc ?= "480ee599fb8df712c10dcf4b7aa6398b79f7d404"
SRCREV_machine_qemux86 ?= "480ee599fb8df712c10dcf4b7aa6398b79f7d404"
SRCREV_machine_qemux86-64 ?= "480ee599fb8df712c10dcf4b7aa6398b79f7d404"
SRCREV_machine_qemumips64 ?= "aee63978005c04ea853099764acaa08130e65554"
SRCREV_machine ?= "480ee599fb8df712c10dcf4b7aa6398b79f7d404"
SRCREV_meta ?= "6acae6f7200af17b3c2be5ecab2cffdc59a02b35"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.9.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.9;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.9.49"

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
