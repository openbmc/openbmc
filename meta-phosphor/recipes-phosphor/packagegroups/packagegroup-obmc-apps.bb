SUMMARY = "OpenBMC - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-bmc-state-mgmt \
        ${PN}-chassis-state-mgmt \
        ${PN}-extras \
        ${PN}-devtools \
        ${PN}-fan-control \
        ${PN}-host-state-mgmt \
        ${PN}-inventory \
        ${PN}-leds \
        ${PN}-logging \
        ${PN}-remote-logging \
        ${PN}-sensors \
        ${PN}-software \
        ${PN}-host-check-mgmt \
        ${PN}-debug-collector \
        ${PN}-settings \
        ${PN}-network \
        ${PN}-user-mgmt \
        "

SUMMARY_${PN}-bmc-state-mgmt = "BMC state management"
RDEPENDS_${PN}-bmc-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-bmc-state-manager} \
        phosphor-state-manager-systemd-target-monitor \
        "

SUMMARY_${PN}-chassis-state-mgmt = "Chassis state management"
RDEPENDS_${PN}-chassis-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-chassis-state-manager} \
        obmc-phosphor-power \
        "

SUMMARY_${PN}-extras = "Extra features"
RDEPENDS_${PN}-extras = " \
        bmcweb \
        phosphor-bmcweb-cert-config \
        phosphor-nslcd-cert-config \
        phosphor-nslcd-authority-cert-config \
        obmc-ikvm \
        phosphor-dbus-monitor \
        phosphor-systemd-policy \
        "

SUMMARY_${PN}-devtools = "Development tools"
RDEPENDS_${PN}-devtools = " \
        bash \
        i2c-tools \
        libgpiod-tools \
        lrzsz \
        rsync \
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

SUMMARY_${PN}-host-state-mgmt = "Host state management"
RDEPENDS_${PN}-host-state-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-host-state-manager} \
        ${VIRTUAL-RUNTIME_obmc-discover-system-state} \
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

SUMMARY_${PN}-user-mgmt = "User management applications"
RDEPENDS_${PN}-user-mgmt = " \
        ${VIRTUAL-RUNTIME_obmc-user-mgmt} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'nss-pam-ldapd', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'phosphor-ldap', '', d)} \
        "
