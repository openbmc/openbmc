require conf/distro/poky.conf
require conf/distro/include/phosphor-defaults.inc

DISTRO = "openbmc-phosphor"
DISTRO_NAME = "Phosphor OpenBMC (Phosphor OpenBMC Project Reference Distro)"
DISTRO_VERSION = "0.1.0"
TARGET_VENDOR="-openbmc"

IMAGE_LINGUAS = "en-us"

VIRTUAL-RUNTIME_keymaps = ""

# Phosphor OpenBMC uses systemd and udev
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = ""

DISTRO_FEATURES_BACKFILL_CONSIDERED = " \
        bluez5 \
        ldconfig \
        gobject-introspection-data \
        pulseaudio \
        sysvinit \
        "

DISTRO_FEATURES_OBMC = " \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-logging-mgmt \
        obmc-phosphor-chassis-mgmt \
        obmc-phosphor-fan-mgmt \
        obmc-phosphor-flash-mgmt \
        obmc-phosphor-system-mgmt \
        obmc-phosphor-user-mgmt \
        obmc-settings-mgmt \
        "

DISTRO_FEATURES_DEFAULT = " \
        avahi \
        pam \
        slp \
        systemd \
        "

DISTRO_FEATURES = " \
        ${DISTRO_FEATURES_DEFAULT} \
        ${DISTRO_FEATURES_LIBC} \
        ${DISTRO_FEATURES_OBMC} \
        "

MACHINE_FEATURES += " \
        ${OBMC_MACHINE_FEATURES} \
        "

DISTRO_EXTRA_RDEPENDS_remove_qemuarm = "packagegroup-core-device-devel"

# TODO: openbmc/openbmc#12345 - Upgrade to pygobject-3.
PREFERRED_VERSION_python-pygobject = "2.%"

include conf/distro/include/openbmc-phosphor/${MACHINE}.inc

INHERIT_remove = " uninative"

KERNEL_FEATURES_append = " phosphor-gpio-keys"
KERNEL_FEATURES_remove_qemuall = " phosphor-gpio-keys"

IMAGE_CLASSES_append = " image_types_phosphor"

# OpenBMC uses a couple recipes from meta-virtualization
# but we do not actually want any virtualization support
# enabled on a BMC distribution.  Silence the emitted
# warning.
SKIP_META_VIRT_SANITY_CHECK = "1"
