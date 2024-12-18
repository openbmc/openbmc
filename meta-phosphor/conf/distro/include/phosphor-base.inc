require conf/distro/include/phosphor-defaults.inc

# Match the distro codename from upstream poky.
DISTRO_CODENAME ?= "scarthgap"

DISTROOVERRIDES .= ":openbmc-phosphor"

QEMU_TARGETS ?= "arm aarch64 i386 riscv32 riscv64 ppc64le x86_64"

PREMIRRORS ??= "\
bzr://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \n \
cvs://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \n \
git://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \n \
gitsm://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \n \
hg://.*/.*    http://downloads.yoctoproject.org/mirror/sources/ \n \
osc://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \n \
p4://.*/.*    http://downloads.yoctoproject.org/mirror/sources/ \n \
svn://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \n"

MIRRORS =+ "\
ftp://.*/.*      http://downloads.yoctoproject.org/mirror/sources/ \n \
http://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
https://.*/.*    http://downloads.yoctoproject.org/mirror/sources/ \n"

# The CONNECTIVITY_CHECK_URI's are used to test whether we can successfully
# fetch from the network (and warn you if not). To disable the test set
# the variable to be empty.
# Git example url: git://git.yoctoproject.org/yocto-firewall-test;protocol=https;rev=master
CONNECTIVITY_CHECK_URIS ?= "https://yoctoproject.org/connectivity.html"

SANITY_TESTED_DISTROS ?= " \
            poky-4.2 \n \
            poky-4.3 \n \
            ubuntu-18.04 \n \
            ubuntu-20.04 \n \
            ubuntu-22.04 \n \
            ubuntu-23.04 \n \
            fedora-38 \n \
            centosstream-8 \n \
            debian-11 \n \
            debian-12 \n \
            opensuseleap-15.4 \n \
            almalinux-8.8 \n \
            almalinux-9.2 \n \
            rocky-9 \n \
            "
#
# OELAYOUT_ABI allows us to notify users when the format of TMPDIR changes in
# an incompatible way. Such changes should usually be detailed in the commit
# that breaks the format and have been previously discussed on the mailing list
# with general agreement from the core team.
#
OELAYOUT_ABI = "15"

# QA check settings - a little stricter than the OE-Core defaults
WARN_TO_ERROR_QA = "already-stripped compile-host-path install-host-path \
                    installed-vs-shipped ldflags pn-overrides rpaths staticdev \
                    useless-rpaths"
WARN_QA:remove = "${WARN_TO_ERROR_QA}"
ERROR_QA:append = " ${WARN_TO_ERROR_QA}"

require conf/distro/include/no-static-libs.inc
require conf/distro/include/yocto-uninative.inc

DISTRO_NAME ?= "Phosphor OpenBMC (Phosphor OpenBMC Project Reference Distro)"
TARGET_VENDOR="-openbmc"


VIRTUAL-RUNTIME_keymaps = ""

# Phosphor OpenBMC uses systemd and udev
INIT_MANAGER = "systemd"
# systemd doesn't like us using /home/root instead of /root but we've been
# doing it a long time.  The `INIT_MANAGER = systemd` switches the default to
# /root, but we should switch it back (and maybe adjust in another commit).
ROOT_HOME = "/home/root"

# We generally want to persist /var/log, which is likely to be used for
# obmc-console and/or journald, unless PHOSPHOR_VOLATILE_VARLOG is set.
FILESYSTEM_PERMS_TABLES:remove = " \
        ${@bb.utils.contains("PHOSPHOR_VOLATILE_VARLOG", "1", "", "files/fs-perms-volatile-log.txt", d)} \
        "

DISTRO_FEATURES_BACKFILL_CONSIDERED = " \
        bluez5 \
        ldconfig \
        gobject-introspection-data \
        pulseaudio \
        "

DISTRO_FEATURES_OBMC = " \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-phosphor-system-mgmt \
        "

DISTRO_FEATURES_DEFAULT = " \
        avahi \
        ipv4 \
        ipv6 \
        pam \
        security \
        slp \
        seccomp \
        "

DISTRO_FEATURES += " \
        ${DISTRO_FEATURES_DEFAULT} \
        ${DISTRO_FEATURES_OBMC} \
        "

DISTRO_EXTRA_RDEPENDS:remove:qemuarm = "packagegroup-core-device-devel"

include conf/distro/include/openbmc-phosphor/${MACHINE}.inc

IMAGE_CLASSES:append = " image_types_phosphor phosphor-rootfs-postcommands"
IMAGE_CLASSES:append:npcm7xx = " image_types_phosphor_nuvoton"
IMAGE_CLASSES:append:npcm8xx = " image_types_phosphor_nuvoton_npcm8xx"

IMAGE_INSTALL:append = " dbus-broker libnss-systemd"

# Skip the udev database by default.  It adds around 2MB
# compressed to the root filesystem, and probably doesn't
# make sense on a BMC anyway.
BAD_RECOMMENDATIONS += "udev-hwdb"
BAD_RECOMMENDATIONS += "shared-mime-info"
BAD_RECOMMENDATIONS += "systemd-mime"

# e2fsprogs isn't needed on a BMC without MMC, where it gets explicitly added
BAD_RECOMMENDATIONS += "e2fsprogs-e2fsck"

LAYER_CONF_VERSION ?= "8"

KERNEL_CLASSES:append = " obmc-phosphor-kernel-version"

# Some packages have directory trees of YAML files arranged based on
# canonical organization names.  'org/freedesktop' and 'xyz/openbmc_project'
# should be used everywhere in phosphor, but some layers may append this with
# their own organization(s).
OBMC_ORG_YAML_SUBDIRS += " org/freedesktop xyz/openbmc_project"

PREFERRED_VERSION_libgpiod = "1.6.5"

# Enable SBOM: https://docs.yoctoproject.org/next/dev-manual/sbom.html
INHERIT += "create-spdx"
