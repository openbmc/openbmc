KBRANCH ?= "v5.4/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH:qemuarm  ?= "v5.4/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v5.4/standard/qemuarm64"
KBRANCH:qemumips ?= "v5.4/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v5.4/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v5.4/standard/base"
KBRANCH:qemux86  ?= "v5.4/standard/base"
KBRANCH:qemux86-64 ?= "v5.4/standard/base"
KBRANCH:qemumips64 ?= "v5.4/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "f367cbe6d0c21c65257c66a4c9b1845fd43285f8"
SRCREV_machine:qemuarm64 ?= "8dcb7ee83e58da8bf51ed8b72165e1ed35beb928"
SRCREV_machine:qemumips ?= "3d4c6263bfdf95960894b75c76aa450d240e3e8e"
SRCREV_machine:qemuppc ?= "125a824c8d14c49b640bc0d6e040d495177caa10"
SRCREV_machine:qemuriscv64 ?= "997c04e7a40084a53bc3d45490949584364697bd"
SRCREV_machine:qemux86 ?= "997c04e7a40084a53bc3d45490949584364697bd"
SRCREV_machine:qemux86-64 ?= "997c04e7a40084a53bc3d45490949584364697bd"
SRCREV_machine:qemumips64 ?= "7082f58984404a5aad90bca1dac4e27773fff26e"
SRCREV_machine ?= "997c04e7a40084a53bc3d45490949584364697bd"
SRCREV_meta ?= "231d3a07e10680c7c89ea101cd803b0684482b11"

# remap qemuarm to qemuarma15 for the 5.4 kernel
# KMACHINE:qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.4;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
LINUX_VERSION ?= "5.4.135"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE:qemuarmv5 = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemumips|qemumips64|qemux86-64|qemuriscv64"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append:qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc"
KERNEL_FEATURES:append:qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append:qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc", "", d)}"
