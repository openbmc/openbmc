# Common code for generating Phosphor OpenBMC images.

# Additional IMAGE_FEATURES available with Phosphor OpenBMC:
#
# - obmc-phosphor-fan-mgmt            - Phosphor OpenBMC fan management
# - obmc-phosphor-chassis-mgmt        - Phosphor OpenBMC chassis management
# - obmc-phosphor-sensor-mgmt         - Phosphor OpenBMC sensor management
# - obmc-phosphor-flash-mgmt          - Phosphor OpenBMC flash management
# - obmc-phosphor-event-mgmt          - Phosphor OpenBMC event management
# - obmc-phosphor-user-mgmt           - Phosphor OpenBMC user management
# - obmc-settings-mgmt                - OpenBMC settings management
# - obmc-phosphor-system-mgmt         - Phosphor OpenBMC system management
# - obmc-host-ipmi                    - OpenBMC Host IPMI
# - obmc-logging                      - OpenBMC logging management

inherit core-image
inherit obmc-phosphor-license
inherit obmc-phosphor-utils

FEATURE_PACKAGES_obmc-fan-mgmt ?= "${@cf_enabled('obmc-phosphor-fan-mgmt', 'virtual-obmc-fan-mgmt', d)}"
FEATURE_PACKAGES_obmc-chassis-mgmt ?= "${@cf_enabled('obmc-phosphor-chassis-mgmt', 'virtual-obmc-chassis-mgmt', d)}"
FEATURE_PACKAGES_obmc-sensor-mgmt ?= "${@cf_enabled('obmc-phosphor-sensor-mgmt', 'virtual-obmc-sensor-mgmt', d)}"
FEATURE_PACKAGES_obmc-flash-mgmt ?= "${@cf_enabled('obmc-phosphor-flash-mgmt', 'virtual-obmc-flash-mgmt', d)}"
FEATURE_PACKAGES_obmc-event-mgmt ?= "${@df_enabled('obmc-phosphor-event-mgmt', 'virtual-obmc-event-mgmt', d)}"
FEATURE_PACKAGES_obmc-user-mgmt ?= "${@df_enabled('obmc-phosphor-user-mgmt', 'virtual-obmc-user-mgmt', d)}"
FEATURE_PACKAGES_obmc-settings-mgmt ?= "${@df_enabled('obmc-settings-mgmt', 'virtual-obmc-settings-mgmt', d)}"
FEATURE_PACKAGES_obmc-system-mgmt ?= "${@df_enabled('obmc-phosphor-system-mgmt', 'virtual-obmc-system-mgmt', d)}"
FEATURE_PACKAGES_obmc-host-ipmi ?= "${@cf_enabled('obmc-host-ipmi', 'virtual-obmc-host-ipmi-hw', d)}"
FEATURE_PACKAGES_obmc-logging-mgmt ?= "${@df_enabled('obmc-logging-mgmt', 'virtual-obmc-logging-mgmt', d)}"
FEATURE_PACKAGES_obmc-host-ctl ?= "${@cf_enabled('obmc-host-ctl', 'virtual-obmc-host-ctl', d)}"

# Install entire Phosphor application stack by default
IMAGE_FEATURES += " \
        obmc-fan-mgmt \
        obmc-chassis-mgmt \
        obmc-sensor-mgmt \
        obmc-flash-mgmt \
        obmc-event-mgmt \
        obmc-user-mgmt \
        obmc-settings-mgmt \
        obmc-system-mgmt \
        obmc-host-ipmi \
        obmc-logging-mgmt \
        obmc-host-ctl \
        ssh-server-dropbear \
        "

CORE_IMAGE_EXTRA_INSTALL_append = " bash \
        packagegroup-phosphor-apps-extras \
        packagegroup-phosphor-apps-extrasdev \
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
