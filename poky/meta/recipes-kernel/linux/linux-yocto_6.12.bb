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

SRCREV_machine:qemuarm ?= "dfeb3f6f2658b9d6ae4d5abebc184bd498dd59e9"
SRCREV_machine:qemuarm64 ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemuloongarch64 ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemumips ?= "8c15ba1838fe0cb24f1ced0b24dedad562dbfd7a"
SRCREV_machine:qemuppc ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemuriscv64 ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemuriscv32 ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemux86 ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemux86-64 ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_machine:qemumips64 ?= "8a34918e22d2fc3b37282d1929e760747762b74e"
SRCREV_machine ?= "c2c450e032c7bf2653e50fc0a87329ce5660b6be"
SRCREV_meta ?= "d36334a8b9597faf3978548085097c3b54d462d1"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "83b4161a63b87ce40d9f24f09b5b006f63d95b7c"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.12/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.12;destsuffix=${KMETA};protocol=https"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.12.23"

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
