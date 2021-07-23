KBRANCH ?= "v5.13/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# board specific branches
KBRANCH_qemuarm  ?= "v5.13/standard/arm-versatile-926ejs"
KBRANCH_qemuarm64 ?= "v5.13/standard/qemuarm64"
KBRANCH_qemumips ?= "v5.13/standard/mti-malta32"
KBRANCH_qemuppc  ?= "v5.13/standard/qemuppc"
KBRANCH_qemuriscv64  ?= "v5.13/standard/base"
KBRANCH_qemuriscv32  ?= "v5.13/standard/base"
KBRANCH_qemux86  ?= "v5.13/standard/base"
KBRANCH_qemux86-64 ?= "v5.13/standard/base"
KBRANCH_qemumips64 ?= "v5.13/standard/mti-malta64"

SRCREV_machine_qemuarm ?= "dc19ba17f4d43a220ae8129312703add02d03d1e"
SRCREV_machine_qemuarm64 ?= "1e086c08b65e8bd1f45f01fd8026599a62deb6c0"
SRCREV_machine_qemumips ?= "4dd19bc8178a6100a2cb9ffd8364e359230253c8"
SRCREV_machine_qemuppc ?= "73c8e406db9beb3a99a5dd3ea67824f0e3c0d7a8"
SRCREV_machine_qemuriscv64 ?= "5e41c505c6057535da2c289d2cc2fec1f64a5068"
SRCREV_machine_qemuriscv32 ?= "5e41c505c6057535da2c289d2cc2fec1f64a5068"
SRCREV_machine_qemux86 ?= "5e41c505c6057535da2c289d2cc2fec1f64a5068"
SRCREV_machine_qemux86-64 ?= "5e41c505c6057535da2c289d2cc2fec1f64a5068"
SRCREV_machine_qemumips64 ?= "0632623fd488acc7c78a4f48d4630caba5e6044e"
SRCREV_machine ?= "5e41c505c6057535da2c289d2cc2fec1f64a5068"
SRCREV_meta ?= "ab5f1940535350791d2e111e0e16b08be277568d"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
DEFAULT_PREFERENCE_class-devupstream = "-1"
SRCREV_machine_class-devupstream ?= "64376a981a0e2e57c46efa63197c2ebb7dab35df"
PN_class-devupstream = "linux-yocto-upstream"
KBRANCH_class-devupstream = "v5.13/base"

# remap qemuarm to qemuarma15 for the 5.8 kernel
# KMACHINE_qemuarm ?= "qemuarma15"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH}; \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.13;destsuffix=${KMETA}"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "5.13.4"

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
