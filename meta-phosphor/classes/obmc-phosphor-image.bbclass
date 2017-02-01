# Common code for generating Phosphor OpenBMC images.

# Additional IMAGE_FEATURES available with Phosphor OpenBMC:
#
# - obmc-bmc-state-mgmt               - OpenBMC BMC state management
# - obmc-chassis-mgmt                 - OpenBMC chassis management
# - obmc-chassis-state-mgmt           - OpenBMC chassis state management
# - obmc-event-mgmt                   - OpenBMC event management
# - obmc-fan-mgmt                     - OpenBMC fan management
# - obmc-flash-mgmt                   - OpenBMC flash management
# - obmc-host-ctl                     - OpenBMC host control
# - obmc-host-ipmi                    - OpenBMC host IPMI
# - obmc-host-state-mgmt              - OpenBMC host state management
# - obmc-logging-mgmt                 - OpenBMC logging management
# - obmc-sensor-mgmt                  - OpenBMC sensor management
# - obmc-settings-mgmt                - OpenBMC settings management
# - obmc-system-mgmt                  - OpenBMC system management
# - obmc-user-mgmt                    - OpenBMC user management

inherit core-image
inherit obmc-phosphor-license
inherit obmc-phosphor-utils

FEATURE_PACKAGES_obmc-bmc-state-mgmt ?= "${@cf_enabled(d, 'obmc-bmc-state-mgmt', 'virtual-obmc-bmc-state-mgmt')}"
FEATURE_PACKAGES_obmc-chassis-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-chassis-mgmt', 'virtual-obmc-chassis-mgmt')}"
FEATURE_PACKAGES_obmc-chassis-state-mgmt ?= "${@cf_enabled(d, 'obmc-chassis-state-mgmt', 'virtual-obmc-chassis-state-mgmt')}"
FEATURE_PACKAGES_obmc-event-mgmt ?= "${@df_enabled(d, 'obmc-phosphor-event-mgmt', 'virtual-obmc-event-mgmt')}"
FEATURE_PACKAGES_obmc-fan-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-fan-mgmt', 'virtual-obmc-fan-mgmt')}"
FEATURE_PACKAGES_obmc-flash-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-flash-mgmt', 'virtual-obmc-flash-mgmt')}"
FEATURE_PACKAGES_obmc-host-ctl ?= "${@cf_enabled(d, 'obmc-host-ctl', 'virtual-obmc-host-ctl')}"
FEATURE_PACKAGES_obmc-host-ipmi ?= "${@cf_enabled(d, 'obmc-host-ipmi', 'virtual-obmc-host-ipmi-hw')}"
FEATURE_PACKAGES_obmc-host-state-mgmt ?= "${@cf_enabled(d, 'obmc-host-state-mgmt', 'virtual-obmc-host-state-mgmt')}"
FEATURE_PACKAGES_obmc-logging-mgmt ?= "${@df_enabled(d, 'obmc-logging-mgmt', 'virtual-obmc-logging-mgmt')}"
FEATURE_PACKAGES_obmc-net-ipmi ?= "${@df_enabled(d, 'obmc-net-ipmi', 'virtual-obmc-net-ipmi')}"
FEATURE_PACKAGES_obmc-sensor-mgmt ?= "${@cf_enabled(d, 'obmc-phosphor-sensor-mgmt', 'virtual-obmc-sensor-mgmt')}"
FEATURE_PACKAGES_obmc-settings-mgmt ?= "${@df_enabled(d, 'obmc-settings-mgmt', 'virtual-obmc-settings-mgmt')}"
FEATURE_PACKAGES_obmc-system-mgmt ?= "${@df_enabled(d, 'obmc-phosphor-system-mgmt', 'virtual-obmc-system-mgmt')}"
FEATURE_PACKAGES_obmc-user-mgmt ?= "${@df_enabled(d, 'obmc-phosphor-user-mgmt', 'virtual-obmc-user-mgmt')}"

# Install entire Phosphor application stack by default
IMAGE_FEATURES += " \
        obmc-bmc-state-mgmt \
        obmc-chassis-mgmt \
        obmc-chassis-state-mgmt \
        obmc-event-mgmt \
        obmc-fan-mgmt \
        obmc-flash-mgmt \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-logging-mgmt \
        obmc-net-ipmi \
        obmc-sensor-mgmt \
        obmc-settings-mgmt \
        obmc-system-mgmt \
        obmc-user-mgmt \
        ssh-server-dropbear \
        "

CORE_IMAGE_EXTRA_INSTALL_append = " bash \
        packagegroup-obmc-apps-extras \
        packagegroup-obmc-apps-extrasdev \
        i2c-tools \
        screen \
        inarp \
        obmc-console \
        pam-plugin-access \
        ${OBMC_IMAGE_EXTRA_INSTALL} \
        ffdc \
        "

OBMC_IMAGE_EXTRA_INSTALL ?= ""

def image_overlay_enabled(d, ifEnabledStr):
        if d.getVar('OBMC_PHOSPHOR_IMAGE_OVERLAY', True) != "1":
            return ""
        return ifEnabledStr

IMAGE_FSTYPES += "${@image_overlay_enabled(d, "overlay")}"
inherit ${@image_overlay_enabled(d, "image-overlay")}

do_image_complete[depends] += "obmc-phosphor-debug-tarball:do_image_complete"
