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

SRCREV_machine_qemuarm ?= "400c0f39b954cd8fffdf53e6ec97852b73fea7af"
SRCREV_machine_qemuarm64 ?= "4d31a8b7661509ff1044abcf9050750cc2478e20"
SRCREV_machine_qemumips ?= "fb03a9472367b6c177729ac631326aafd5d17c92"
SRCREV_machine_qemuppc ?= "4d31a8b7661509ff1044abcf9050750cc2478e20"
SRCREV_machine_qemux86 ?= "4d31a8b7661509ff1044abcf9050750cc2478e20"
SRCREV_machine_qemux86-64 ?= "4d31a8b7661509ff1044abcf9050750cc2478e20"
SRCREV_machine_qemumips64 ?= "26b8ba186a6d39728fc1510bd2264110c75842f5"
SRCREV_machine ?= "4d31a8b7661509ff1044abcf9050750cc2478e20"
SRCREV_meta ?= "b149d14ccae8349ab33e101f6af233a12f4b17ba"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.4.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.4;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.4.113"

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
