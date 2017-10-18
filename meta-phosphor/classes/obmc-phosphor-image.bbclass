# Common code for generating Phosphor OpenBMC images.

# Additional IMAGE_FEATURES available with Phosphor OpenBMC:
#
# - obmc-bmc-state-mgmt               - OpenBMC BMC state management
# - obmc-chassis-mgmt                 - OpenBMC chassis management
# - obmc-chassis-state-mgmt           - OpenBMC chassis state management
# - obmc-fan-control                  - OpenBMC fan management
# - obmc-fan-mgmt                     - Deprecated - use obmc-fan-control instead
# - obmc-flash-mgmt                   - OpenBMC flash management
# - obmc-host-ctl                     - OpenBMC host control
# - obmc-host-ipmi                    - OpenBMC host IPMI
# - obmc-host-state-mgmt              - OpenBMC host state management
# - obmc-host-check-mgmt              - OpenBMC host state checking
# - obmc-inventory                    - OpenBMC inventory support
# - obmc-leds                         - OpenBMC LED support
# - obmc-logging-mgmt                 - OpenBMC logging management
# - obmc-sensors                      - OpenBMC sensor support
# - obmc-settings-mgmt                - OpenBMC settings management
# - obmc-software                     - OpenBMC software management
# - obmc-system-mgmt                  - OpenBMC system management
# - obmc-user-mgmt                    - OpenBMC user management
# - obmc-debug-collector              - OpenBMC debug collector

inherit core-image
inherit obmc-phosphor-license
inherit obmc-phosphor-utils

# TODO: openbmc/openbmc#1407 - Remove with RHEL6 support deprecation.
#     Set supported kernel back to RHEL6.4's kernel.
SDK_OLDEST_KERNEL = "2.6.32"

FEATURE_PACKAGES_obmc-bmc-state-mgmt ?= "packagegroup-obmc-apps-bmc-state-mgmt"
FEATURE_PACKAGES_obmc-chassis-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-chassis-mgmt', 'virtual-obmc-chassis-mgmt')}"
FEATURE_PACKAGES_obmc-chassis-state-mgmt ?= "packagegroup-obmc-apps-chassis-state-mgmt"
FEATURE_PACKAGES_obmc-fan-control ?= "packagegroup-obmc-apps-fan-control"
FEATURE_PACKAGES_obmc-fan-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-fan-mgmt', 'virtual-obmc-fan-mgmt')}"
FEATURE_PACKAGES_obmc-flash-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-flash-mgmt', 'virtual-obmc-flash-mgmt')}"
FEATURE_PACKAGES_obmc-host-ctl ?= "${@cf_enabled(d, 'obmc-host-ctl', 'virtual-obmc-host-ctl')}"
FEATURE_PACKAGES_obmc-host-ipmi ?= "${@cf_enabled(d, 'obmc-host-ipmi', 'virtual-obmc-host-ipmi-hw')}"
FEATURE_PACKAGES_obmc-host-state-mgmt ?= "packagegroup-obmc-apps-host-state-mgmt"
FEATURE_PACKAGES_obmc-host-check-mgmt ?= "packagegroup-obmc-apps-host-check-mgmt"
FEATURE_PACKAGES_obmc-inventory ?= "packagegroup-obmc-apps-inventory"
FEATURE_PACKAGES_obmc-leds ?= "packagegroup-obmc-apps-leds"
FEATURE_PACKAGES_obmc-logging-mgmt ?= "${@df_enabled(d, 'obmc-logging-mgmt', 'virtual-obmc-logging-mgmt')}"
FEATURE_PACKAGES_obmc-net-ipmi ?= "${@df_enabled(d, 'obmc-net-ipmi', 'virtual-obmc-net-ipmi')}"
FEATURE_PACKAGES_obmc-sensors ?= "packagegroup-obmc-apps-sensors"
FEATURE_PACKAGES_obmc-settings-mgmt ?= "${@df_enabled(d, 'obmc-settings-mgmt', 'virtual-obmc-settings-mgmt')}"
FEATURE_PACKAGES_obmc-software ?= "packagegroup-obmc-apps-software"
FEATURE_PACKAGES_obmc-system-mgmt ?= "${@df_enabled(d, 'obmc-phosphor-system-mgmt', 'virtual-obmc-system-mgmt')}"
FEATURE_PACKAGES_obmc-debug-collector ?= "packagegroup-obmc-apps-debug-collector"
FEATURE_PACKAGES_obmc-settings ?= "packagegroup-obmc-apps-settings"
FEATURE_PACKAGES_obmc-network-mgmt ?= "packagegroup-obmc-apps-network"
FEATURE_PACKAGES_obmc-user-mgmt ?= "packagegroup-obmc-apps-user-mgmt"

# Install entire Phosphor application stack by default
IMAGE_FEATURES += " \
        obmc-bmc-state-mgmt \
        obmc-chassis-mgmt \
        obmc-chassis-state-mgmt \
        obmc-fan-control \
        obmc-fan-mgmt \
        obmc-flash-mgmt \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-host-check-mgmt \
        obmc-inventory \
        obmc-leds \
        obmc-logging-mgmt \
        obmc-net-ipmi \
        obmc-sensors \
        obmc-settings-mgmt \
        obmc-software \
        obmc-system-mgmt \
        obmc-user-mgmt \
        ssh-server-dropbear \
        obmc-debug-collector \
        obmc-network-mgmt \
        obmc-settings \
        ${@mf_enabled(d, 'obmc-ubi-fs', 'read-only-rootfs')} \
        "

CORE_IMAGE_EXTRA_INSTALL_append = " bash \
        packagegroup-obmc-apps-extras \
        packagegroup-obmc-apps-extrasdev \
        i2c-tools \
        screen \
        obmc-console \
        pam-plugin-access \
        ${OBMC_IMAGE_EXTRA_INSTALL} \
        ffdc \
        rsync \
        rng-tools \
        "

OBMC_IMAGE_EXTRA_INSTALL ?= ""

do_image_complete[depends] += "obmc-phosphor-debug-tarball:do_image_complete"

# The /etc/version file is misleading and not useful.  Remove it.
# Users should instead rely on /etc/os-release.
remove_etc_version() {
        rm ${IMAGE_ROOTFS}${sysconfdir}/version
}
ROOTFS_POSTPROCESS_COMMAND += "remove_etc_version ; "
