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

SRCREV_machine:qemuarm ?= "07ca2c1cc013343f9a47b5ac4f37ed60f66fd73b"
SRCREV_machine:qemuarm64 ?= "7af45f35fdcc82bbff07fa3d031620d5f5728b6b"
SRCREV_machine:qemuloongarch64 ?= "e53dc7514de7d2fbe0f80547a50c0542928e2d11"
SRCREV_machine:qemumips ?= "e0b08aab2ccc4257f0b34e7dcb1e054ea188a43d"
SRCREV_machine:qemuppc ?= "9e609ca17604c708fdc6e7e5c9355bb2c5b73bcd"
SRCREV_machine:qemuriscv64 ?= "e53dc7514de7d2fbe0f80547a50c0542928e2d11"
SRCREV_machine:qemuriscv32 ?= "e53dc7514de7d2fbe0f80547a50c0542928e2d11"
SRCREV_machine:qemux86 ?= "e53dc7514de7d2fbe0f80547a50c0542928e2d11"
SRCREV_machine:qemux86-64 ?= "e53dc7514de7d2fbe0f80547a50c0542928e2d11"
SRCREV_machine:qemumips64 ?= "58ffd9a4a907262daaedd9aca1e95e65d9716de3"
SRCREV_machine ?= "e53dc7514de7d2fbe0f80547a50c0542928e2d11"
SRCREV_meta ?= "3b1f87ec237ec3ad9acffb3d75c55efe958085dc"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "4631960b4700dd53f5cebb4f7055fd00ccd556ce"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.5/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.5;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.5.13"

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

