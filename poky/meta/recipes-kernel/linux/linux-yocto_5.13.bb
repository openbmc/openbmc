KBRANCH ?= "v5.13/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH:qemuarm  ?= "v5.13/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v5.13/standard/qemuarm64"
KBRANCH:qemumips ?= "v5.13/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v5.13/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v5.13/standard/base"
KBRANCH:qemuriscv32  ?= "v5.13/standard/base"
KBRANCH:qemux86  ?= "v5.13/standard/base"
KBRANCH:qemux86-64 ?= "v5.13/standard/base"
KBRANCH:qemumips64 ?= "v5.13/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "7bf87cf7827b03e8eca31063b118ebc11d5c5ee5"
SRCREV_machine:qemuarm64 ?= "448966e7f96755f3b735a43c3d3895cb29c0e76e"
SRCREV_machine:qemumips ?= "6fd77ca515af94b780f49d6403a0bafdee1c60c2"
SRCREV_machine:qemuppc ?= "14ecf9f266afdcfd8ca9308ed90f4a7a5a2adcd0"
SRCREV_machine:qemuriscv64 ?= "49ec738aa7f6a59d11b46f61dea82965018f3124"
SRCREV_machine:qemuriscv32 ?= "49ec738aa7f6a59d11b46f61dea82965018f3124"
SRCREV_machine:qemux86 ?= "49ec738aa7f6a59d11b46f61dea82965018f3124"
SRCREV_machine:qemux86-64 ?= "49ec738aa7f6a59d11b46f61dea82965018f3124"
SRCREV_machine:qemumips64 ?= "95713ce4053b8acd170fc99503f9b076e5f3cec3"
SRCREV_machine ?= "49ec738aa7f6a59d11b46f61dea82965018f3124"
SRCREV_meta ?= "c38435a3cacf424fa686ecac9a95ef8349b83bb3"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
DEFAULT_PREFERENCE:class-devupstream = "-1"
SRCREV_machine:class-devupstream ?= "f428e49b8cb1fbd9b4b4b29ea31b6991d2ff7de1"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v5.13/base"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE:qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.13;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.13.12"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"
DEPENDS += "gmp-native"

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
