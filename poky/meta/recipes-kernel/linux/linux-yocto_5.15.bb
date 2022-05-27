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

SRCREV_machine:qemuarm ?= "5a68f2d15d17f0f3c397e7f8c83f3f664f7037e5"
SRCREV_machine:qemuarm64 ?= "00e666e6154fcdf52268f2a5a612b96afad073b0"
SRCREV_machine:qemumips ?= "fb9e75076deade31754b7ad644952d63137e616b"
SRCREV_machine:qemuppc ?= "49f6567f3b85a843e8b6042a79c58aab0bdbd0c9"
SRCREV_machine:qemuriscv64 ?= "cc9695f5fd3b520464eb2ded66950734f308525c"
SRCREV_machine:qemuriscv32 ?= "cc9695f5fd3b520464eb2ded66950734f308525c"
SRCREV_machine:qemux86 ?= "cc9695f5fd3b520464eb2ded66950734f308525c"
SRCREV_machine:qemux86-64 ?= "cc9695f5fd3b520464eb2ded66950734f308525c"
SRCREV_machine:qemumips64 ?= "1ad01ab47ec056d4126798f6d57a33b65b2be49c"
SRCREV_machine ?= "cc9695f5fd3b520464eb2ded66950734f308525c"
SRCREV_meta ?= "37891dc371e83a3451781dd81a8a85cccd60084b"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "3fbf24b73f4a5bc8fd39a6b7a29145451c1039ce"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v5.15/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.15;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.15.38"

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
KERNEL_FEATURES:append:powerpc =" arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64 =" arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64le =" arch/powerpc/powerpc-debug.scc"

INSANE_SKIP:kernel-vmlinux:qemuppc64 = "textrel"

