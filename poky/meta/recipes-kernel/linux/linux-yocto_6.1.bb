KBRANCH ?= "v6.1/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# CVE exclusions
include recipes-kernel/linux/cve-exclusion_6.1.inc

# board specific branches
KBRANCH:qemuarm  ?= "v6.1/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v6.1/standard/qemuarm64"
KBRANCH:qemumips ?= "v6.1/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v6.1/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v6.1/standard/base"
KBRANCH:qemuriscv32  ?= "v6.1/standard/base"
KBRANCH:qemux86  ?= "v6.1/standard/base"
KBRANCH:qemux86-64 ?= "v6.1/standard/base"
KBRANCH:qemuloongarch64  ?= "v6.1/standard/base"
KBRANCH:qemumips64 ?= "v6.1/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "0b80e90b38ae1735c7dab701ca3d0b2447376ccc"
SRCREV_machine:qemuarm64 ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemuloongarch64 ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemumips ?= "db61d7fe3540904fbe77b532ce3e37aeb737524a"
SRCREV_machine:qemuppc ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemuriscv64 ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemuriscv32 ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemux86 ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemux86-64 ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_machine:qemumips64 ?= "aacc52b6216955723cebb5bc493a4210357b23b2"
SRCREV_machine ?= "581dc1aa2f340fff2cc010067257185fa2c993f9"
SRCREV_meta ?= "36901b5b298e601fe73dd79aaff8b615a7762013"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "f17b0ab65d17988d5e6d6fe22f708ef3721080bf"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.1/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.1;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.1.25"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"
DEPENDS += "gmp-native libmpc-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE:qemuarmv5 = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "^(qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemuppc64|qemumips|qemumips64|qemux86-64|qemuriscv64|qemuriscv32|qemuloongarch64)$"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append:qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc cfg/net/mdio.scc"
KERNEL_FEATURES:append:qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append:qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc", "", d)}"
KERNEL_FEATURES:append:powerpc =" arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64 =" arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64le =" arch/powerpc/powerpc-debug.scc"

INSANE_SKIP:kernel-vmlinux:qemuppc64 = "textrel"

