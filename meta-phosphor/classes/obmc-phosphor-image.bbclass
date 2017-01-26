# Common code for generating Phosphor OpenBMC images.

# Additional IMAGE_FEATURES available with Phosphor OpenBMC:
#
# - obmc-chassis-mgmt                 - OpenBMC chassis management
# - obmc-chassis-state-mgmt           - OpenBMC chassis state management
# - obmc-event-mgmt                   - OpenBMC event management
# - obmc-fan-mgmt                     - OpenBMC fan management
# - obmc-flash-mgmt                   - OpenBMC flash management
# - obmc-host-ctl                     - OpenBMC host control
# - obmc-host-ipmi                    - OpenBMC host IPMI
# - obmc-host-state-mgmt              - OpenBMC host state management
# - obmc-inventory                    - OpenBMC inventory support
# - obmc-leds                         - OpenBMC LED support
# - obmc-logging-mgmt                 - OpenBMC logging management
# - obmc-sensor-mgmt                  - OpenBMC sensor management
# - obmc-sensors                      - OpenBMC sensor support
# - obmc-settings-mgmt                - OpenBMC settings management
# - obmc-system-mgmt                  - OpenBMC system management
# - obmc-user-mgmt                    - OpenBMC user management

inherit core-image
inherit obmc-phosphor-license
inherit obmc-phosphor-utils

FEATURE_PACKAGES_obmc-chassis-mgmt ?= "${@cf_enabled('obmc-phosphor-chassis-mgmt', 'virtual-obmc-chassis-mgmt', d)}"
FEATURE_PACKAGES_obmc-chassis-state-mgmt ?= "${@cf_enabled('obmc-chassis-state-mgmt', 'virtual-obmc-chassis-state-mgmt', d)}"
FEATURE_PACKAGES_obmc-event-mgmt ?= "${@df_enabled('obmc-phosphor-event-mgmt', 'virtual-obmc-event-mgmt', d)}"
FEATURE_PACKAGES_obmc-fan-mgmt ?= "${@cf_enabled('obmc-phosphor-fan-mgmt', 'virtual-obmc-fan-mgmt', d)}"
FEATURE_PACKAGES_obmc-flash-mgmt ?= "${@cf_enabled('obmc-phosphor-flash-mgmt', 'virtual-obmc-flash-mgmt', d)}"
FEATURE_PACKAGES_obmc-host-ctl ?= "${@cf_enabled('obmc-host-ctl', 'virtual-obmc-host-ctl', d)}"
FEATURE_PACKAGES_obmc-host-ipmi ?= "${@cf_enabled('obmc-host-ipmi', 'virtual-obmc-host-ipmi-hw', d)}"
FEATURE_PACKAGES_obmc-host-state-mgmt ?= "${@cf_enabled('obmc-host-state-mgmt', 'virtual-obmc-host-state-mgmt', d)}"
FEATURE_PACKAGES_obmc-inventory ?= "packagegroup-obmc-apps-inventory"
FEATURE_PACKAGES_obmc-leds ?= "packagegroup-obmc-apps-leds"
FEATURE_PACKAGES_obmc-logging-mgmt ?= "${@df_enabled('obmc-logging-mgmt', 'virtual-obmc-logging-mgmt', d)}"
FEATURE_PACKAGES_obmc-sensor-mgmt ?= "${@cf_enabled('obmc-phosphor-sensor-mgmt', 'virtual-obmc-sensor-mgmt', d)}"
FEATURE_PACKAGES_obmc-sensors ?= "packagegroup-obmc-apps-sensors"
FEATURE_PACKAGES_obmc-settings-mgmt ?= "${@df_enabled('obmc-settings-mgmt', 'virtual-obmc-settings-mgmt', d)}"
FEATURE_PACKAGES_obmc-system-mgmt ?= "${@df_enabled('obmc-phosphor-system-mgmt', 'virtual-obmc-system-mgmt', d)}"
FEATURE_PACKAGES_obmc-user-mgmt ?= "${@df_enabled('obmc-phosphor-user-mgmt', 'virtual-obmc-user-mgmt', d)}"

# Install entire Phosphor application stack by default
IMAGE_FEATURES += " \
        obmc-chassis-mgmt \
        obmc-chassis-state-mgmt \
        obmc-event-mgmt \
        obmc-fan-mgmt \
        obmc-flash-mgmt \
        obmc-host-ctl \
        obmc-host-ipmi \
        obmc-host-state-mgmt \
        obmc-inventory \
        obmc-leds \
        obmc-logging-mgmt \
        obmc-sensor-mgmt \
        obmc-sensors \
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
        "

OBMC_IMAGE_EXTRA_INSTALL ?= ""

def image_overlay_enabled(d, ifEnabledStr):
        if d.getVar('OBMC_PHOSPHOR_IMAGE_OVERLAY', True) != "1":
            return ""
        return ifEnabledStr

IMAGE_FSTYPES += "${@image_overlay_enabled(d, "overlay")}"
inherit ${@image_overlay_enabled(d, "image-overlay")}

do_image_complete[depends] += "obmc-phosphor-debug-tarball:do_image_complete"
