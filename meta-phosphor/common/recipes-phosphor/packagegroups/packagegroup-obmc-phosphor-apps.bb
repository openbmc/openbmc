SUMMARY = "Phosphor OpenBMC - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        packagegroup-obmc-phosphor-apps-extras \
        packagegroup-obmc-phosphor-apps-extrasdev \
        ${@cf_enabled("obmc-phosphor-fan-mgmt", "packagegroup-obmc-phosphor-apps-fan-mgmt", d)} \
        ${@cf_enabled("obmc-phosphor-chassis-mgmt", "packagegroup-obmc-phosphor-apps-chassis-mgmt", d)} \
        ${@cf_enabled("obmc-phosphor-sensor-mgmt", "packagegroup-obmc-phosphor-apps-sensor-mgmt", d)} \
        ${@cf_enabled("obmc-phosphor-flash-mgmt", "packagegroup-obmc-phosphor-apps-flash-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-event-mgmt", "packagegroup-obmc-phosphor-apps-event-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-user-mgmt", "packagegroup-obmc-phosphor-apps-user-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-system-mgmt", "packagegroup-obmc-phosphor-apps-system-mgmt", d)} \
        "

SUMMARY_packagegroup-obmc-phosphor-apps-extras = "Extra features"
RDEPENDS_packagegroup-obmc-phosphor-apps-extras = " \
	phosphor-rest \
        host-ipmid \
        "

SUMMARY_packagegroup-obmc-phosphor-apps-extrasdev = "Development features"
RDEPENDS_packagegroup-obmc-phosphor-apps-extrasdev = " \
        rest-dbus \
        "

SUMMARY_packagegroup-obmc-phosphor-apps-fan-mgmt = "Fan management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-fan-mgmt = \
        "${@cf_enabled("obmc-phosphor-fan-mgmt", " \
                virtual-obmc-fan-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-chassis-mgmt = "Chassis management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-chassis-mgmt = "\
        ${@cf_enabled("obmc-phosphor-chassis-mgmt", " \
                virtual-obmc-chassis-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-sensor-mgmt = "Sensor management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-sensor-mgmt = "\
        ${@cf_enabled("obmc-phosphor-sensor-mgmt", " \
                virtual-obmc-sensor-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-flash-mgmt = "Flash management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-flash-mgmt = "\
        ${@cf_enabled("obmc-phosphor-flash-mgmt", " \
                virtual-obmc-flash-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-event-mgmt = "Event management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-event-mgmt = " \
        ${@df_enabled("obmc-phosphor-event-mgmt", " \
                virtual-obmc-event-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-user-mgmt = "User management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-user-mgmt = " \
        ${@df_enabled("obmc-phosphor-user-mgmt", " \
                virtual-obmc-user-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-system-mgmt = "System management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-system-mgmt = " \
        ${@df_enabled("obmc-phosphor-system-mgmt", " \
                virtual-obmc-system-mgmt \
        ", d)}"
