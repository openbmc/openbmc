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

SRCREV_machine:qemuarm ?= "04942abac8568705f1fae34066db171b6e2669bd"
SRCREV_machine:qemuarm64 ?= "ea4b620f18f882b3d882a53ffa33d8125ab27c83"
SRCREV_machine:qemuloongarch64 ?= "14f83e40930806c3f5c61988e69a3ca1820a1b8f"
SRCREV_machine:qemumips ?= "3348b580e3c47da56ce97a8297a574c2e37bc410"
SRCREV_machine:qemuppc ?= "2fd47e07960edcd21455548ac6a25b19babe5c10"
SRCREV_machine:qemuriscv64 ?= "14f83e40930806c3f5c61988e69a3ca1820a1b8f"
SRCREV_machine:qemuriscv32 ?= "14f83e40930806c3f5c61988e69a3ca1820a1b8f"
SRCREV_machine:qemux86 ?= "14f83e40930806c3f5c61988e69a3ca1820a1b8f"
SRCREV_machine:qemux86-64 ?= "14f83e40930806c3f5c61988e69a3ca1820a1b8f"
SRCREV_machine:qemumips64 ?= "6706327d870a0f246df8ed20c6a7f51ef46db1d6"
SRCREV_machine ?= "14f83e40930806c3f5c61988e69a3ca1820a1b8f"
SRCREV_meta ?= "9af846da534077c91e3c42242fceba7aef8dd784"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "121c6addffd71815cbd333baf409be682e2e148f"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.5/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.5;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.5.7"

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

