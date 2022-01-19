KBRANCH ?= "v5.14/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH:qemuarm  ?= "v5.14/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v5.14/standard/qemuarm64"
KBRANCH:qemumips ?= "v5.14/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v5.14/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v5.14/standard/base"
KBRANCH:qemuriscv32  ?= "v5.14/standard/base"
KBRANCH:qemux86  ?= "v5.14/standard/base"
KBRANCH:qemux86-64 ?= "v5.14/standard/base"
KBRANCH:qemumips64 ?= "v5.14/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "5ca7fd91b258a07ed1b6f38593ff8c48cc574b1c"
SRCREV_machine:qemuarm64 ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_machine:qemumips ?= "1e7a6d0d29015bf1f383cf5f52fc451c1969561d"
SRCREV_machine:qemuppc ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_machine:qemuriscv64 ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_machine:qemuriscv32 ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_machine:qemux86 ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_machine:qemux86-64 ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_machine:qemumips64 ?= "f6646a344afbf6cacc91cbeaaec4240b372dd192"
SRCREV_machine ?= "9d5572038eacda2e2a86e3f743f35ec415319fb4"
SRCREV_meta ?= "f9e349e174542980f72dcd087445d0106b7a5e75"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
DEFAULT_PREFERENCE:class-devupstream = "-1"
SRCREV_machine:class-devupstream ?= "545728d9e08593767dd55192b0324dd4f9b71151"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v5.14/base"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE:qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.14;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.14.21"

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
