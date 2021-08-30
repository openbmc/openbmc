KBRANCH ?= "v5.10/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v5.10/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v5.10/standard/qemuarm64"
KBRANCH_qemumips ?= "v5.10/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v5.10/standard/qemuppc"
KBRANCH_qemuriscv64  ?= "v5.10/standard/base"
KBRANCH_qemuriscv32  ?= "v5.10/standard/base"
KBRANCH_qemux86  ?= "v5.10/standard/base"
KBRANCH_qemux86-64 ?= "v5.10/standard/base"
KBRANCH_qemumips64 ?= "v5.10/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "21075c593dd7a09fc2e0fe4c1f751999fee1127a"
SRCREV_machine_qemuarm64 ?= "e32f43fed15419c8461207c4d2b76879920d5928"
SRCREV_machine_qemumips ?= "127501aba35af6e38f50ecd814da4416f361fd84"
SRCREV_machine_qemuppc ?= "219057449c55acde1060af4b63c2d1ba5ec19978"
SRCREV_machine_qemuriscv64 ?= "b1ff0bb0de7abc5039e0db14f66e01eb0a3c24bb"
SRCREV_machine_qemuriscv32 ?= "b1ff0bb0de7abc5039e0db14f66e01eb0a3c24bb"
SRCREV_machine_qemux86 ?= "b1ff0bb0de7abc5039e0db14f66e01eb0a3c24bb"
SRCREV_machine_qemux86-64 ?= "b1ff0bb0de7abc5039e0db14f66e01eb0a3c24bb"
SRCREV_machine_qemumips64 ?= "dd28c0cc8a79329b8b724821e7c09b210a2e2948"
SRCREV_machine ?= "b1ff0bb0de7abc5039e0db14f66e01eb0a3c24bb"
SRCREV_meta ?= "22257690910a1befc2ed8a98ef218bd0c5cfd844"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.10;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.10.57"

DEPENDS += "${@bb.utils.contains('ARCH', 'x86', 'elfutils-native', '', d)}"
DEPENDS += "openssl-native util-linux-native"
DEPENDS += "gmp-native"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE_qemuarmv5 = "versatile-pb.dtb"

COMPATIBLE_MACHINE = "qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemuppc64|qemumips|qemumips64|qemux86-64|qemuriscv64|qemuriscv32"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc", "", d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc", "", d)}"
