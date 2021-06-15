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

SUMMARY_${PN}-bmc-state-mgmt = "BMC state management"
RDEPENDS_${PN}-bmc-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-state-manager} \
        phosphor-state-manager-systemd-target-monitor \
        "

SUMMARY_${PN}-bmcweb = "bmcweb support"
RDEPENDS_${PN}-bmcweb = " \
        bmcweb \
        phosphor-bmcweb-cert-config \
        "

SUMMARY_${PN}-chassis-state-mgmt = "Chassis state management"
RDEPENDS_${PN}-chassis-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-chassis-state-manager} \
        obmc-phosphor-power \
        "

SUMMARY_${PN}-console = "Serial over LAN support"
RDEPENDS_${PN}-console = " \
        obmc-console \
        "

# Deprecated - add new packages to an existing packagegroup or create a new one.
SUMMARY_${PN}-extras = "Extra features"
RDEPENDS_${PN}-extras = ""

SUMMARY_${PN}-devtools = "Development tools"
RDEPENDS_${PN}-devtools = " \
        bash \
        ffdc \
        i2c-tools \
        libgpiod-tools \
        lrzsz \
        rsync \
        "

SUMMARY_${PN}-dbus-monitor = "Support for dbus monitoring"
RDEPENDS_${PN}-dbus-monitor = " \
        phosphor-dbus-monitor \
        "

# Use the fan control package group for applications
# implementing fan control or system fan policy only.
# Applications that create inventory or sensors should
# be added those respective package groups instead.
SUMMARY_${PN}-fan-control = "Fan control"
RDEPENDS_${PN}-fan-control = " \
        ${VIRTUAL-RUNTIME_obmc-fan-control} \
        phosphor-fan-monitor \
        "

SUMMARY_${PN}-fru-ipmi = "Support for EEPROMS with IPMI FRU"
RDEPENDS_${PN}-fru-ipmi = " \
        fru-device \
        "

SUMMARY_${PN}-health-monitor = "Support for health monitoring"
RDEPENDS_${PN}-health-monitor = " \
        phosphor-health-monitor \
        "

SUMMARY_${PN}-host-state-mgmt = "Host state management"
RDEPENDS_${PN}-host-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-host-state-manager} \
        ${VIRTUAL-RUNTIME_obmc-discover-system-state} \
        "

SUMMARY_${PN}-ikvm = "KVM over IP support"
RDEPENDS_${PN}-ikvm = " \
        obmc-ikvm \
        "

SUMMARY_${PN}-inventory = "Inventory applications"
RDEPENDS_${PN}-inventory = " \
        ${VIRTUAL-RUNTIME_obmc-inventory-manager} \
        ${VIRTUAL-RUNTIME_obmc-fan-presence} \
        "

SUMMARY_${PN}-leds = "LED applications"
RDEPENDS_${PN}-leds = " \
        ${VIRTUAL-RUNTIME_obmc-leds-manager} \
        ${VIRTUAL-RUNTIME_obmc-leds-sysfs} \
        ${VIRTUAL-RUNTIME_obmc-led-monitor} \
        "

SUMMARY_${PN}-logging = "Logging applications"
RDEPENDS_${PN}-logging = " \
        phosphor-logging \
        "

SUMMARY_${PN}-remote-logging = "Remote logging applications"
RDEPENDS_${PN}-remote-logging = " \
        rsyslog \
        phosphor-rsyslog-config \
        "

SUMMARY_${PN}-rng = "Random Number Generator support"
RDEPENDS_${PN}-rng = " \
        rng-tools \
        "

SUMMARY_${PN}-sensors = "Sensor applications"
RDEPENDS_${PN}-sensors = " \
        ${VIRTUAL-RUNTIME_obmc-sensors-hwmon} \
        "

${PN}-software-extras = ""

${PN}-software-extras_df-obmc-ubi-fs = " \
        phosphor-software-manager-updater-ubi \
        "

${PN}-software-extras_df-phosphor-mmc = " \
        phosphor-software-manager-updater-mmc \
        "

SUMMARY_${PN}-software = "Software applications"
RDEPENDS_${PN}-software = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-download-mgr} \
        ${VIRTUAL-RUNTIME_obmc-bmc-updater} \
        ${VIRTUAL-RUNTIME_obmc-bmc-version} \
        ${${PN}-software-extras} \
        "

SUMMARY_${PN}-debug-collector = "BMC debug collector"
RDEPENDS_${PN}-debug-collector = " \
        ${VIRTUAL-RUNTIME_obmc-dump-manager} \
        ${VIRTUAL-RUNTIME_obmc-dump-monitor} \
        phosphor-debug-collector-dreport \
        phosphor-debug-collector-scripts \
        "

SUMMARY_${PN}-settings = "Settings applications"
RDEPENDS_${PN}-settings = " \
        phosphor-settings-manager \
        "

SUMMARY_${PN}-network = "BMC Network Manager"
RDEPENDS_${PN}-network = " \
        ${VIRTUAL-RUNTIME_obmc-network-manager} \
        "

SUMMARY_${PN}-telemetry = "Telemetry solution"
RDEPENDS_${PN}-telemetry = " \
        telemetry \
        "

SUMMARY_${PN}-user-mgmt = "User management applications"
RDEPENDS_${PN}-user-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-user-mgmt} \
        "
RRECOMMENDS_${PN}-user-mgmt = " \
        pam-plugin-access \
        "

SUMMARY_${PN}-user-mgmt-ldap = "LDAP users and groups support"
RDEPENDS_${PN}-user-mgmt-ldap = " \
        ${PN}-user-mgmt \
        nss-pam-ldapd \
        phosphor-ldap \
        phosphor-nslcd-cert-config \
        phosphor-nslcd-authority-cert-config \
        "
