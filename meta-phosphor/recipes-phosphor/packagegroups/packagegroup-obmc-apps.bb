SUMMARY = "OpenBMC - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-bmc-state-mgmt \
        ${PN}-bmcweb \
        ${PN}-chassis-state-mgmt \
        ${PN}-console \
        ${PN}-dbus-monitor \
        ${PN}-extras \
        ${PN}-devtools \
        ${PN}-fan-control \
        ${PN}-fru-ipmi \
        ${PN}-health-monitor \
        ${PN}-host-state-mgmt \
        ${PN}-ikvm \
        ${PN}-inventory \
        ${PN}-leds \
        ${PN}-logging \
        ${PN}-remote-logging \
        ${PN}-rng \
        ${PN}-sensors \
        ${PN}-software \
        ${PN}-host-check-mgmt \
        ${PN}-debug-collector \
        ${PN}-settings \
        ${PN}-network \
        ${PN}-telemetry \
        ${PN}-user-mgmt \
        ${PN}-user-mgmt-ldap \
        "

SUMMARY:${PN}-bmc-state-mgmt = "BMC state management"
RDEPENDS:${PN}-bmc-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-state-manager} \
        phosphor-state-manager-systemd-target-monitor \
        obmc-targets \
        "

SUMMARY:${PN}-bmcweb = "bmcweb support"
RDEPENDS:${PN}-bmcweb = " \
        bmcweb \
        phosphor-certificate-manager \
        "

SUMMARY:${PN}-chassis-state-mgmt = "Chassis state management"
RDEPENDS:${PN}-chassis-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-chassis-state-manager} \
        obmc-phosphor-power \
        "

SUMMARY:${PN}-console = "Serial over LAN support"
RDEPENDS:${PN}-console = " \
        obmc-console \
        "

# Deprecated - add new packages to an existing packagegroup or create a new one.
SUMMARY:${PN}-extras = "Extra features"
RDEPENDS:${PN}-extras = ""

SUMMARY:${PN}-devtools = "Development tools"
RDEPENDS:${PN}-devtools = " \
        bash \
        ffdc \
        i2c-tools \
        libgpiod-tools \
        lrzsz \
        rsync \
        trace-enable \
        "

SUMMARY:${PN}-dbus-monitor = "Support for dbus monitoring"
RDEPENDS:${PN}-dbus-monitor = " \
        phosphor-dbus-monitor \
        "

# Use the fan control package group for applications
# implementing fan control or system fan policy only.
# Applications that create inventory or sensors should
# be added those respective package groups instead.
SUMMARY:${PN}-fan-control = "Fan control"
RDEPENDS:${PN}-fan-control = " \
        ${VIRTUAL-RUNTIME_obmc-fan-control} \
        phosphor-fan-monitor \
        "

SUMMARY:${PN}-fru-ipmi = "Support for EEPROMS with IPMI FRU"
RDEPENDS:${PN}-fru-ipmi = " \
        fru-device \
        "

SUMMARY:${PN}-health-monitor = "Support for health monitoring"
RDEPENDS:${PN}-health-monitor = " \
        phosphor-health-monitor \
        "

SUMMARY:${PN}-host-state-mgmt = "Host state management"
RDEPENDS:${PN}-host-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-host-state-manager} \
        ${VIRTUAL-RUNTIME_obmc-discover-system-state} \
        "

SUMMARY:${PN}-ikvm = "KVM over IP support"
RDEPENDS:${PN}-ikvm = " \
        obmc-ikvm \
        "

SUMMARY:${PN}-inventory = "Inventory applications"
RDEPENDS:${PN}-inventory = " \
        ${VIRTUAL-RUNTIME_obmc-inventory-manager} \
        ${VIRTUAL-RUNTIME_obmc-fan-presence} \
        "

SUMMARY:${PN}-leds = "LED applications"
RDEPENDS:${PN}-leds = " \
        phosphor-led-manager \
        phosphor-led-sysfs \
        phosphor-led-manager-faultmonitor \
        "

SUMMARY:${PN}-logging = "Logging applications"
RDEPENDS:${PN}-logging = " \
        phosphor-logging \
        "

SUMMARY:${PN}-remote-logging = "Remote logging applications"
RDEPENDS:${PN}-remote-logging = " \
        rsyslog \
        phosphor-rsyslog-config \
        "

SUMMARY:${PN}-rng = "Random Number Generator support"
RDEPENDS:${PN}-rng = " \
        rng-tools \
        "

SUMMARY:${PN}-sensors = "Sensor applications"
RDEPENDS:${PN}-sensors = " \
        ${VIRTUAL-RUNTIME_obmc-sensors-hwmon} \
        "

${PN}-software-extras = ""

${PN}-software-extras:df-obmc-ubi-fs = " \
        phosphor-software-manager-updater-ubi \
        "

${PN}-software-extras:df-phosphor-mmc = " \
        phosphor-software-manager-updater-mmc \
        "

SUMMARY:${PN}-software = "Software applications"
RDEPENDS:${PN}-software = " \
        phosphor-software-manager-download-mgr \
        phosphor-software-manager-updater \
        phosphor-software-manager-version \
        ${${PN}-software-extras} \
        "

SUMMARY:${PN}-debug-collector = "BMC debug collector"
RDEPENDS:${PN}-debug-collector = " \
        phosphor-debug-collector-manager \
        phosphor-debug-collector-monitor \
        phosphor-debug-collector-dreport \
        phosphor-debug-collector-scripts \
        "

SUMMARY:${PN}-settings = "Settings applications"
RDEPENDS:${PN}-settings = " \
        phosphor-settings-manager \
        "

SUMMARY:${PN}-network = "BMC Network Manager"
RDEPENDS:${PN}-network = " \
        phosphor-network \
        "

SUMMARY:${PN}-telemetry = "Telemetry solution"
RDEPENDS:${PN}-telemetry = " \
        telemetry \
        "

SUMMARY:${PN}-user-mgmt = "User management applications"
RDEPENDS:${PN}-user-mgmt = " \
        phosphor-user-manager \
        "
RRECOMMENDS:${PN}-user-mgmt = " \
        pam-plugin-access \
        "

SUMMARY:${PN}-user-mgmt-ldap = "LDAP users and groups support"
RDEPENDS:${PN}-user-mgmt-ldap = " \
        ${PN}-user-mgmt \
        nss-pam-ldapd \
        phosphor-ldap \
        "
