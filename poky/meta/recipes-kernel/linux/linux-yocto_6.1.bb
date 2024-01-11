KBRANCH ?= "v6.1/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# CVE exclusions
include recipes-kernel/linux/cve-exclusion.inc
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

SRCREV_machine:qemuarm ?= "85915187700314cb7ac70fd33da3e9dfd7c20063"
SRCREV_machine:qemuarm64 ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemuloongarch64 ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemumips ?= "24b06ee00fc3b65a24d7e867148b08a85296e67c"
SRCREV_machine:qemuppc ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemuriscv64 ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemuriscv32 ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemux86 ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemux86-64 ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_machine:qemumips64 ?= "d4659a339611a02e4ffc2861e697c1a278707d70"
SRCREV_machine ?= "db1e71dc5c31557828fae0084b0f9cc83882eacd"
SRCREV_meta ?= "991713c8765172cb5d18703d15589f3ec6e1b772"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "ba6f5fb465114fcd48ddb2c7a7740915b2289d6b"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.1/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.1;destsuffix=${KMETA};protocol=https"
SRC_URI += "file://0001-perf-cpumap-Make-counter-as-unsigned-ints.patch"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.1.68"

PV = "${LINUX_VERSION}+git"

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

