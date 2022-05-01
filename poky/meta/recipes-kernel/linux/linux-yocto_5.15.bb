KBRANCH ?= "v5.15/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH:qemuarm  ?= "v5.15/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v5.15/standard/qemuarm64"
KBRANCH:qemumips ?= "v5.15/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v5.15/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v5.15/standard/base"
KBRANCH:qemuriscv32  ?= "v5.15/standard/base"
KBRANCH:qemux86  ?= "v5.15/standard/base"
KBRANCH:qemux86-64 ?= "v5.15/standard/base"
KBRANCH:qemumips64 ?= "v5.15/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "b6fd1a7dd80a336567fa30c1d674f0d5df9af836"
SRCREV_machine:qemuarm64 ?= "387a676543764b59e38cf6b13d6474846fb07d78"
SRCREV_machine:qemumips ?= "7c084cf3a700f7a2c68c8909501f4d35b3215e40"
SRCREV_machine:qemuppc ?= "239f7c8f37bf9ade16325101df3c06a485ccc74e"
SRCREV_machine:qemuriscv64 ?= "c9f3902d8069e32a8928153a38d8f6115194d128"
SRCREV_machine:qemuriscv32 ?= "c9f3902d8069e32a8928153a38d8f6115194d128"
SRCREV_machine:qemux86 ?= "c9f3902d8069e32a8928153a38d8f6115194d128"
SRCREV_machine:qemux86-64 ?= "c9f3902d8069e32a8928153a38d8f6115194d128"
SRCREV_machine:qemumips64 ?= "a4805fe749c9c56d18a60b5378674760ef0e85ed"
SRCREV_machine ?= "c9f3902d8069e32a8928153a38d8f6115194d128"
SRCREV_meta ?= "63e25b5717751b4b33685bd5991d10c52934a4c6"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "e29be6724adbc9c3126d2a9550ec21f927f22f6d"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v5.15/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.15;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.15.32"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"
DEPENDS += "gmp-native libmpc-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE:qemuarmv5 = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemuppc64|qemumips|qemumips64|qemux86-64|qemuriscv64|qemuriscv32"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append:qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc"
KERNEL_FEATURES:append:qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append:qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc", "", d)}"

INSANE_SKIP:kernel-vmlinux:qemuppc64 = "textrel"

