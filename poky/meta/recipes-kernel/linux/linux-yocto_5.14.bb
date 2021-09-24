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

SRCREV_machine:qemuarm ?= "8226a3a65df2dbae0fe71e9ff54cba70a9ba85e5"
SRCREV_machine:qemuarm64 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:qemumips ?= "b5389debd85300e24b877f25c2e90381f1df7678"
SRCREV_machine:qemuppc ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:qemuriscv64 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:qemuriscv32 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:qemux86 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:qemux86-64 ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_machine:qemumips64 ?= "56cc67b699194944809832f4c8f58b9828f02bf9"
SRCREV_machine ?= "7ae156be3bdbf033839f7f3ec2e9a0ffffb18818"
SRCREV_meta ?= "42d2cf670ed06f4dddd2a035611a519ea68e2d26"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
DEFAULT_PREFERENCE:class-devupstream = "-1"
SRCREV_machine:class-devupstream ?= "6a7ababc0268063d0798c46d5859a90ee996612f"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v5.14/base"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE:qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.14;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.14.6"

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
