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

SRCREV_machine_qemuarm ?= "c8ab8bf2bdff3b355710065725f4e272bf0a5a5d"
SRCREV_machine_qemuarm64 ?= "fec49247816d7045aa8abe0047bcd4737af9a853"
SRCREV_machine_qemumips ?= "475a0182d9e940351a3fecaecb31e55effad45ad"
SRCREV_machine_qemuppc ?= "88a3e5a3a8f693d25b09b64cd2be5df783933f74"
SRCREV_machine_qemux86 ?= "fec49247816d7045aa8abe0047bcd4737af9a853"
SRCREV_machine_qemux86-64 ?= "fec49247816d7045aa8abe0047bcd4737af9a853"
SRCREV_machine_qemumips64 ?= "9ffd7155a07e7cfed2229b3a78d2278f3b56f13d"
SRCREV_machine ?= "fec49247816d7045aa8abe0047bcd4737af9a853"
SRCREV_meta ?= "44719fa8f73fd7c444044ad3c04f5fc66f57b993"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.1.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.1;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.1.36"

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
