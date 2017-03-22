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

SRCREV_machine_qemuarm ?= "2bb25d9e29ff91d3266e6abe12c6c1a6b6508e98"
SRCREV_machine_qemuarm64 ?= "4b1b4bfb6cf27609b37eb44cd7fee700cd402123"
SRCREV_machine_qemumips ?= "d116a8bdd1d90fe3dde092f119a2dde1674b02c4"
SRCREV_machine_qemuppc ?= "ef0e5cfd16edcb52ec2e3d5abdcb97d8881b2c3d"
SRCREV_machine_qemux86 ?= "021b4aef55b44597587a1ce5879be642b3dca155"
SRCREV_machine_qemux86-64 ?= "021b4aef55b44597587a1ce5879be642b3dca155"
SRCREV_machine_qemumips64 ?= "1b20066b88c3abdde1f6c449b6562a516b4cce6d"
SRCREV_machine ?= "021b4aef55b44597587a1ce5879be642b3dca155"
SRCREV_meta ?= "926c93ae07de2173b4f764d1da6996597a6d2b73"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.8.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.8;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.8.12"

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
