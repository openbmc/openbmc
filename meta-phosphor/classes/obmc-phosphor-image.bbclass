# Common code for generating Phosphor OpenBMC images.

# Additional IMAGE_FEATURES available with Phosphor OpenBMC:
#
# - obmc-bmc-state-mgmt               - OpenBMC BMC state management
# - obmc-bmcweb                       - OpenBMC webserver
# - obmc-chassis-mgmt                 - OpenBMC chassis management
# - obmc-chassis-state-mgmt           - OpenBMC chassis state management
# - obmc-console                      - OpenBMC serial over LAN
# - obmc-dbus-monitor                 - OpenBMC dbus monitoring
# - obmc-debug-collector              - OpenBMC debug collector
# - obmc-devtools                     - OpenBMC development and debugging tools
# - obmc-fan-control                  - OpenBMC fan management
# - obmc-fan-mgmt                     - Deprecated - use obmc-fan-control instead
# - obmc-flash-mgmt                   - OpenBMC flash management
# - obmc-health-monitor               - OpenBMC health monitoring
# - obmc-host-ctl                     - OpenBMC host control
# - obmc-host-ipmi                    - OpenBMC host IPMI
# - obmc-host-state-mgmt              - OpenBMC host state management
# - obmc-ikvm                         - OpenBMC KVM over IP
# - obmc-inventory                    - OpenBMC inventory support
# - obmc-leds                         - OpenBMC LED support
# - obmc-logging-mgmt                 - OpenBMC logging management
# - obmc-remote-logging-mgmt          - OpenBMC remote logging management
# - obmc-sensors                      - OpenBMC sensor support
# - obmc-settings-mgmt                - OpenBMC settings management
# - obmc-software                     - OpenBMC software management
# - obmc-system-mgmt                  - OpenBMC system management
# - obmc-telemetry                    - OpenBMC telemetry solution
# - obmc-user-mgmt                    - OpenBMC user management
# - obmc-user-mgmt-ldap               - OpenBMC LDAP users
# - obmc-webui                        - OpenBMC Web User Interface

inherit core-image
inherit obmc-phosphor-utils

FEATURE_PACKAGES_obmc-bmc-state-mgmt ?= "packagegroup-obmc-apps-bmc-state-mgmt"
FEATURE_PACKAGES_obmc-bmcweb ?= "packagegroup-obmc-apps-bmcweb"
FEATURE_PACKAGES_obmc-chassis-mgmt ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-phosphor-chassis-mgmt', 'virtual-obmc-chassis-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-chassis-state-mgmt ?= "packagegroup-obmc-apps-chassis-state-mgmt"
FEATURE_PACKAGES_obmc-console ?= "packagegroup-obmc-apps-console"
FEATURE_PACKAGES_obmc-dbus-monitor ?= "packagegroup-obmc-apps-dbus-monitor"
FEATURE_PACKAGES_obmc-devtools ?= "packagegroup-obmc-apps-devtools"
FEATURE_PACKAGES_obmc-fan-control ?= "packagegroup-obmc-apps-fan-control"
FEATURE_PACKAGES_obmc-fan-mgmt ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-phosphor-fan-mgmt', 'virtual-obmc-fan-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-flash-mgmt ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-phosphor-flash-mgmt', 'virtual-obmc-flash-mgmt', '', d)}"
FEATURE_PACKAGES_obmc-health-monitor ?= "packagegroup-obmc-apps-health-monitor"
FEATURE_PACKAGES_obmc-host-ctl ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-host-ctl', 'virtual-obmc-host-ctl', '', d)}"
FEATURE_PACKAGES_obmc-host-ipmi ?= "${@bb.utils.contains('COMBINED_FEATURES', 'obmc-host-ipmi', 'virtual-obmc-host-ipmi-hw', '', d)}"
FEATURE_PACKAGES_obmc-host-state-mgmt ?= "packagegroup-obmc-apps-host-state-mgmt"
FEATURE_PACKAGES_obmc-ikvm ?= "packagegroup-obmc-apps-ikvm"
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
FEATURE_PACKAGES_obmc-telemetry ?= "packagegroup-obmc-apps-telemetry"
FEATURE_PACKAGES_obmc-user-mgmt ?= "packagegroup-obmc-apps-user-mgmt"
FEATURE_PACKAGES_obmc-user-mgmt-ldap ?= "packagegroup-obmc-apps-user-mgmt-ldap"
FEATURE_PACKAGES_obmc-dmtf-pmci ?= "packagegroup-obmc-apps-dmtf-pmci"

# Note that the webui is not included by default in OpenBMC
# images due to its non-standard build process. It utilizes
# npm during the build, resulting in an inability to build
# this package offline and making the software bill of materials
# incorrect.
FEATURE_PACKAGES_obmc-webui ?= "packagegroup-obmc-apps-webui"

# FIXME: phosphor-net-ipmi depends on phosphor-ipmi-host !?!? and
# cannot be built on core-qemu machines because of the dependency
# tree under phosphor-ipmi-host
FEATURE_PACKAGES_obmc-net-ipmi:qemuall = ""

# EVB systems do not have a managed system.
FEATURE_PACKAGES_obmc-system-mgmt:phosphor-evb = ""
# QEMU systems are like EVBs and do not have a managed system.
FEATURE_PACKAGES_obmc-system-mgmt:qemuall = ""

# Add new packages to be installed to a package group in
# packagegroup-obmc-apps, not here.
OBMC_IMAGE_BASE_INSTALL = " \
        packagegroup-obmc-apps-extras \
        ${OBMC_IMAGE_EXTRA_INSTALL} \
        "

OBMC_IMAGE_EXTRA_INSTALL ?= ""

CORE_IMAGE_EXTRA_INSTALL += "${OBMC_IMAGE_BASE_INSTALL}"

remove_etc_version() {
        rm ${IMAGE_ROOTFS}${sysconfdir}/version
}

enable_ldap_nsswitch() {
    sed -i 's/\(\(passwd\|group\):\s*\).*/\1files systemd ldap/' \
        "${IMAGE_ROOTFS}${sysconfdir}/nsswitch.conf"
    sed -i 's/\(shadow:\s*\).*/\1files ldap/' \
        "${IMAGE_ROOTFS}${sysconfdir}/nsswitch.conf"
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('IMAGE_FEATURES', 'obmc-user-mgmt-ldap', 'enable_ldap_nsswitch', '', d)}"

