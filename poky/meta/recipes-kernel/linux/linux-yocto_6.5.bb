KBRANCH ?= "v6.5/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# CVE exclusions
include recipes-kernel/linux/cve-exclusion.inc
include recipes-kernel/linux/cve-exclusion_6.5.inc

# board specific branches
KBRANCH:qemuarm  ?= "v6.5/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v6.5/standard/qemuarm64"
KBRANCH:qemumips ?= "v6.5/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v6.5/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v6.5/standard/base"
KBRANCH:qemuriscv32  ?= "v6.5/standard/base"
KBRANCH:qemux86  ?= "v6.5/standard/base"
KBRANCH:qemux86-64 ?= "v6.5/standard/base"
KBRANCH:qemuloongarch64  ?= "v6.5/standard/base"
KBRANCH:qemumips64 ?= "v6.5/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "eb2eba60c726fd5c0257422e55117f46f792f72e"
SRCREV_machine:qemuarm64 ?= "e709bc7ca85655ff5317fbcf4c8cb69365b6fc40"
SRCREV_machine:qemuloongarch64 ?= "128116621dee1ddbc7cf5f58cddc708d7b823600"
SRCREV_machine:qemumips ?= "0524fc1380569f39eb5473cc8344ce2e4e78a82d"
SRCREV_machine:qemuppc ?= "0b23c1d5705f5efa91b3757377ec284b1d42a84a"
SRCREV_machine:qemuriscv64 ?= "128116621dee1ddbc7cf5f58cddc708d7b823600"
SRCREV_machine:qemuriscv32 ?= "128116621dee1ddbc7cf5f58cddc708d7b823600"
SRCREV_machine:qemux86 ?= "128116621dee1ddbc7cf5f58cddc708d7b823600"
SRCREV_machine:qemux86-64 ?= "128116621dee1ddbc7cf5f58cddc708d7b823600"
SRCREV_machine:qemumips64 ?= "b63329018323409369e952c84ac6481e68a9b6af"
SRCREV_machine ?= "128116621dee1ddbc7cf5f58cddc708d7b823600"
SRCREV_meta ?= "e4aaaaddfaf695039a7fc41815e24f57c29e30c0"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "43a868577dfcd9be6930453dc5ec557ab2e9ad89"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.5/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.5;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.5.10"

PV = "${LINUX_VERSION}+git"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE:qemuarmv5 = "arm/versatile-pb.dtb"

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

