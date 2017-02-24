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

SRCREV_machine_qemuarm ?= "4cc544ad09ad704322cb66fe4ba197a6a05dc71f"
SRCREV_machine_qemuarm64 ?= "1adf9d36338dc3c63cdbf6f98bcbdc7bba42a794"
SRCREV_machine_qemumips ?= "c285969d4f9376a671167ecf397578c8ad3e6a75"
SRCREV_machine_qemuppc ?= "1adf9d36338dc3c63cdbf6f98bcbdc7bba42a794"
SRCREV_machine_qemux86 ?= "1adf9d36338dc3c63cdbf6f98bcbdc7bba42a794"
SRCREV_machine_qemux86-64 ?= "1adf9d36338dc3c63cdbf6f98bcbdc7bba42a794"
SRCREV_machine_qemumips64 ?= "64f96ba530e58456070f26b0f3fcce3f64988b72"
SRCREV_machine ?= "1adf9d36338dc3c63cdbf6f98bcbdc7bba42a794"
SRCREV_meta ?= "83110d94edeb856a3667b62903ed4ae91c24117d"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-4.8.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-4.8;destsuffix=${KMETA}"

LINUX_VERSION ?= "4.8.3"

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
