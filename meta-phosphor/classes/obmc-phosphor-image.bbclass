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
# - obmc-inventory                    - OpenBMC inventory support
# - obmc-leds                         - OpenBMC LED support
# - obmc-logging-mgmt                 - OpenBMC logging management
# - obmc-remote-logging-mgmt          - OpenBMC remote logging management
# - obmc-sensors                      - OpenBMC sensor support
# - obmc-settings-mgmt                - OpenBMC settings management
# - obmc-software                     - OpenBMC software management
# - obmc-system-mgmt                  - OpenBMC system management
# - obmc-user-mgmt                    - OpenBMC user management
# - obmc-debug-collector              - OpenBMC debug collector

inherit core-image

FEATURE_PACKAGES_obmc-bmc-state-mgmt ?= "packagegroup-obmc-apps-bmc-state-mgmt"
FEATURE_PACKAGES_obmc-chassis-mgmt ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-phosphor-chassis-mgmt', 'virtual-obmc-chassis-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-chassis-state-mgmt ?= "packagegroup-obmc-apps-chassis-state-mgmt"
FEATURE_PACKAGES_obmc-fan-control ?= "packagegroup-obmc-apps-fan-control"
FEATURE_PACKAGES_obmc-fan-mgmt ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-phosphor-fan-mgmt', 'virtual-obmc-fan-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-flash-mgmt ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-phosphor-flash-mgmt', 'virtual-obmc-flash-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-host-ctl ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-host-ctl', 'virtual-obmc-host-ctl', '', d)}"
FEATURE_PACKAGES_obmc-host-ipmi ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-host-ipmi', 'virtual-obmc-host-ipmi-hw', '', d)}"
FEATURE_PACKAGES_obmc-host-state-mgmt ?= "packagegroup-obmc-apps-host-state-mgmt"
FEATURE_PACKAGES_obmc-inventory ?= "packagegroup-obmc-apps-inventory"
FEATURE_PACKAGES_obmc-leds ?= "packagegroup-obmc-apps-leds"
FEATURE_PACKAGES_obmc-logging-mgmt ?= "packagegroup-obmc-apps-logging"
FEATURE_PACKAGES_obmc-remote-logging-mgmt ?= "packagegroup-obmc-apps-remote-logging"
FEATURE_PACKAGES_obmc-net-ipmi ?= "phosphor-ipmi-net"
FEATURE_PACKAGES_obmc-sensors ?= "packagegroup-obmc-apps-sensors"
FEATURE_PACKAGES_obmc-software ?= "packagegroup-obmc-apps-software"
FEATURE_PACKAGES_obmc-system-mgmt ?= "${@bb.utils.contains('DISTRO_FEATURES', 'obmc-phosphor-system-mgmt', 'virtual-obmc-system-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-debug-collector ?= "packagegroup-obmc-apps-debug-collector"
FEATURE_PACKAGES_obmc-settings-mgmt ?= "packagegroup-obmc-apps-settings"
FEATURE_PACKAGES_obmc-network-mgmt ?= "packagegroup-obmc-apps-network"
FEATURE_PACKAGES_obmc-user-mgmt ?= "packagegroup-obmc-apps-user-mgmt"

# FIXME: phosphor-net-ipmi depends on phosphor-ipmi-host !?!? and
# cannot be built on core-qemu machines because of the dependency
# tree under phosphor-ipmi-host
FEATURE_PACKAGES_obmc-net-ipmi_qemuall = ""

CORE_IMAGE_EXTRA_INSTALL_append = " bash \
        packagegroup-obmc-apps-extras \
        packagegroup-obmc-apps-extrasdevtools \
        i2c-tools \
        obmc-console \
        pam-plugin-access \
        ${OBMC_IMAGE_EXTRA_INSTALL} \
        ffdc \
        rsync \
        rng-tools \
        lrzsz \
        "

OBMC_IMAGE_EXTRA_INSTALL ?= ""

remove_etc_version() {
        rm ${IMAGE_ROOTFS}${sysconfdir}/version
}

disable_systemd_pager() {
        echo "SYSTEMD_PAGER=" >> ${IMAGE_ROOTFS}${sysconfdir}/profile
        echo "export SYSTEMD_PAGER" >> ${IMAGE_ROOTFS}${sysconfdir}/profile
}
