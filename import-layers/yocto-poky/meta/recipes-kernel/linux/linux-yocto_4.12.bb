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

SRCREV_machine_qemuarm ?= "6e5a99db3a495e023041a30d43262047795e645a"
SRCREV_machine_qemuarm64 ?= "40146055677a69730b2c36da1c8c1b4e9bae7bb0"
SRCREV_machine_qemumips ?= "43dc47f90007d54c7086cc03b28e946e30135f1c"
SRCREV_machine_qemuppc ?= "40146055677a69730b2c36da1c8c1b4e9bae7bb0"
SRCREV_machine_qemux86 ?= "40146055677a69730b2c36da1c8c1b4e9bae7bb0"
SRCREV_machine_qemux86-64 ?= "40146055677a69730b2c36da1c8c1b4e9bae7bb0"
SRCREV_machine_qemumips64 ?= "69f0c96d8e47b0dccfb374809d729f0042c77868"
SRCREV_machine ?= "40146055677a69730b2c36da1c8c1b4e9bae7bb0"
SRCREV_meta ?= "19d815d5a34bfaad95d87cc097cef18b594daac8"

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
