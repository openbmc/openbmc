# This recipe is intended as a 'simpler' replacement for packagegroup-base.
# Please communicate your use cases and suggestions to the mailinglist(s)

SUMMARY = "Basic task to get a device online"

PR = "r13"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

# Poke extra recomendations into the list using your machine.conf
#
MACHINE_EXTRA_RRECOMMENDS ?= ""

#
# Select between dropbear and openssh
# Set TASK_BASIC_SSHDAEMON = "openssh-sshd openssh-sftp openssh-sftp-server" in your DISTRO config to get openssh(d)
#
TASK_BASIC_SSHDAEMON ?= "dropbear openssh-sftp openssh-sftp-server"

#
# The section below is designed to match with packagegroup-boot, but doesn't depend on it to allow for more freedom
# when writing image recipes.
# It also avoids the choice between connman/networkmanager/ifupdown since that is an image feature, not a
# distro feature.
#
# Util-linux (u)mount is included because the busybox one can't handle /etc/mtab being symlinked to /proc/mounts
#
RDEPENDS_${PN} = "\
    ${TASK_BASIC_SSHDAEMON} \
    avahi-daemon avahi-utils \
"

#
# The following section is split in 3:
#   1) Machine features: kernel modules and userspace helpers for those
#   2) Distro features: packages associated with those
#   3) Nice to have: packages that are nice to have, but aren't strictly needed
#
RRECOMMENDS_${PN} = "\
    ${MACHINE_EXTRA_RRECOMMENDS} \
    ${@bb.utils.contains("MACHINE_FEATURES", "usbhost", "usbutils", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "alsa", "alsa-utils-alsamixer", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "usbgadget", "kernel-module-g-ether kernel-module-g-serial kernel-module-g-mass-storage", "", d)} \
    \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluetooth", "bluez5", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "wifi", "iw wpa-supplicant", "", d)} \
    \
    tzdata \
    \
    cpufrequtils \
    htop \
"
