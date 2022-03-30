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

SRCREV_machine:qemuarm ?= "4632211e5b019b6337536305bfce0a5ffd3b570b"
SRCREV_machine:qemuarm64 ?= "c88fcdb0b3ca81f0149309ed7025cb28f709ed3d"
SRCREV_machine:qemumips ?= "ad268b2a1d6d1d6f1c9e9103cf4b1519477feb39"
SRCREV_machine:qemuppc ?= "688bb26b6f83a60ec39a0f20f31ec2bc37b49c6f"
SRCREV_machine:qemuriscv64 ?= "70cf8dde55448a804da825139fa12daf5a855047"
SRCREV_machine:qemuriscv32 ?= "70cf8dde55448a804da825139fa12daf5a855047"
SRCREV_machine:qemux86 ?= "70cf8dde55448a804da825139fa12daf5a855047"
SRCREV_machine:qemux86-64 ?= "70cf8dde55448a804da825139fa12daf5a855047"
SRCREV_machine:qemumips64 ?= "5e7bcff3dccd4749783b87d69ffd405ba71c9fda"
SRCREV_machine ?= "70cf8dde55448a804da825139fa12daf5a855047"
SRCREV_meta ?= "fee71fc34f2e551ebfd7bf0996d82f3447787e7a"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "0464ab17184b8fdec6676fabe76059b90e54e74f"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v5.15/base"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE:qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.15;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.15.30"

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

