KBRANCH ?= "standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "standard/qemuarm64"
KBRANCH_qemumips ?= "standard/mti-malta32"
KBRANCH_qemuppc  ?= "standard/qemuppc"
KBRANCH_qemux86  ?= "standard/common-pc/base"
KBRANCH_qemux86-64 ?= "standard/common-pc-64/base"
KBRANCH_qemumips64 ?= "standard/mti-malta64"

SRCREV_machine_qemuarm ?= "4817747912b5c50ce5c31ef25658340ca615e1b4"
SRCREV_machine_qemuarm64 ?= "578602a722dbfb260801f3b37c6eafd2abb2340d"
SRCREV_machine_qemumips ?= "6ed76ec26b120f65f8547c8612b7334bd2745ec9"
SRCREV_machine_qemuppc ?= "a86ade84b2e142c0fd7536d96477107b6d07db5c"
SRCREV_machine_qemux86 ?= "d9bf859dfae6f88b88b157119c20ae4d5e51420a"
SRCREV_machine_qemux86-64 ?= "93b2b800d85c1565af7d96f3776dc38c85ae1902"
SRCREV_machine_qemumips64 ?= "a63d40b860a6d255005a541894d53729090b40ea"
SRCREV_machine ?= "578602a722dbfb260801f3b37c6eafd2abb2340d"
SRCREV_meta ?= "060fa80b7996250001ee90c50a4978c8fdb87fc4"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-3.14.git;branch=${KBRANCH};name=machine; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-3.14;destsuffix=${KMETA}"

LINUX_VERSION ?= "3.14.36"
LINUX_VERSION_qemux86 ?= "3.14.39"
LINUX_VERSION_qemux86-64 ?= "3.14.39"

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
