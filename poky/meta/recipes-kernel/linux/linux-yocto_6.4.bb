KBRANCH ?= "v6.4/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# CVE exclusions
include recipes-kernel/linux/cve-exclusion.inc
include recipes-kernel/linux/cve-exclusion_6.4.inc

# board specific branches
KBRANCH:qemuarm  ?= "v6.4/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v6.4/standard/qemuarm64"
KBRANCH:qemumips ?= "v6.4/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v6.4/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v6.4/standard/base"
KBRANCH:qemuriscv32  ?= "v6.4/standard/base"
KBRANCH:qemux86  ?= "v6.4/standard/base"
KBRANCH:qemux86-64 ?= "v6.4/standard/base"
KBRANCH:qemuloongarch64  ?= "v6.4/standard/base"
KBRANCH:qemumips64 ?= "v6.4/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "16af0b21320a78b21d5d9ded1188e398352d262a"
SRCREV_machine:qemuarm64 ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemuloongarch64 ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemumips ?= "de46701cb3ac494b27ae70f1475efb855e9d817a"
SRCREV_machine:qemuppc ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemuriscv64 ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemuriscv32 ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemux86 ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemux86-64 ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_machine:qemumips64 ?= "47d7881e76d678cc9dc034f0acdd1bc416fa05bb"
SRCREV_machine ?= "72bad8cd7540f07ab54e08b83ad106dec0df123c"
SRCREV_meta ?= "88ed9ec49099d69f9546d21137191fd747d06ec4"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "38ca69782268c8e9578ba2f1fccf931f643eb8da"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.4/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.4;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.4.9"

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

