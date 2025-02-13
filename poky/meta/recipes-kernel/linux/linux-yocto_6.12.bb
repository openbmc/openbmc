KBRANCH ?= "v6.12/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# CVE exclusions
include recipes-kernel/linux/cve-exclusion.inc
include recipes-kernel/linux/cve-exclusion_6.12.inc

# board specific branches
KBRANCH:qemuarm  ?= "v6.12/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v6.12/standard/base"
KBRANCH:qemumips ?= "v6.12/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v6.12/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v6.12/standard/base"
KBRANCH:qemuriscv32  ?= "v6.12/standard/base"
KBRANCH:qemux86  ?= "v6.12/standard/base"
KBRANCH:qemux86.104 ?= "v6.12/standard/base"
KBRANCH:qemuloongarch64  ?= "v6.12/standard/base"
KBRANCH:qemumips64 ?= "v6.12/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "416d14d80c6a4cb641d42f95140aff1ede4da75a"
SRCREV_machine:qemuarm64 ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemuloongarch64 ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemumips ?= "529b1f229abddc1db8f3240a19a0352257cdea49"
SRCREV_machine:qemuppc ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemuriscv64 ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemuriscv32 ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemux86 ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemux86-64 ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_machine:qemumips64 ?= "10419c638ea26a6dbd364c913f7c3d6894c64e23"
SRCREV_machine ?= "c58d3ea5bbce394208d8099e9d6783bb0a0ddd25"
SRCREV_meta ?= "2506ff7d20ee515e70964844fa40b35e4fdfbe92"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "4b07fe4a044d863926707e1106ff142427ec6e02"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.12/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.12;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.12.13"

PV = "${LINUX_VERSION}+git"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE:qemuarmv5 = "arm/versatile-pb.dtb"

COMPATIBLE_MACHINE = "^(qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemuppc64|qemumips|qemumips64|qemux86-64|qemuriscv64|qemuriscv32|qemuloongarch64)$"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append:qemuall = " cfg/virtio.scc features/drm-bochs/drm-bochs.scc cfg/net/mdio.scc"
KERNEL_FEATURES:append:qemux86 = " cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append:qemux86-64 = " cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc features/nf_tables/nft_test.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc features/gpio/sim.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("KERNEL_DEBUG", "True", " features/reproducibility/reproducibility.scc features/debug/debug-btf.scc", "", d)}"
# libteam ptests from meta-oe needs it
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/net/team/team.scc", "", d)}"
# openl2tp tests from meta-networking needs it
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " cgl/cfg/net/l2tp.scc", "", d)}"
KERNEL_FEATURES:append:powerpc = " arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64 = " arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64le = " arch/powerpc/powerpc-debug.scc"

INSANE_SKIP:kernel-vmlinux:qemuppc64 = "textrel"
