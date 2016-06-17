SUMMARY = "Phosphor OpenBMC - Applications"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

VIRTUAL-RUNTIME_obmc-phosphor-fan-ctl ?= "virtual/obmc-phosphor-fan-ctl"
VIRTUAL-RUNTIME_obmc-phosphor-sensor-ctl ?= "virtual/obmc-phosphor-sensor-ctl"
VIRTUAL-RUNTIME_obmc-phosphor-chassis-ctl ?= "virtual/obmc-phosphor-chassis-ctl"
VIRTUAL-RUNTIME_obmc-phosphor-flash-ctl ?= "virtual/obmc-phosphor-flash-ctl"

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        packagegroup-obmc-phosphor-apps-extras \
        packagegroup-obmc-phosphor-apps-extrasdev \
        ${@mf_enabled("obmc-phosphor-fan-mgmt", "packagegroup-obmc-phosphor-apps-fan-mgmt", d)} \
        ${@mf_enabled("obmc-phosphor-chassis-mgmt", "packagegroup-obmc-phosphor-apps-chassis-mgmt", d)} \
        ${@mf_enabled("obmc-phosphor-sensor-mgmt", "packagegroup-obmc-phosphor-apps-sensor-mgmt", d)} \
        ${@mf_enabled("obmc-phosphor-flash-mgmt", "packagegroup-obmc-phosphor-apps-flash-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-event-mgmt", "packagegroup-obmc-phosphor-apps-event-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-policy-mgmt", "packagegroup-obmc-phosphor-apps-policy-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-user-mgmt", "packagegroup-obmc-phosphor-apps-user-mgmt", d)} \
        ${@df_enabled("obmc-phosphor-system-mgmt", "packagegroup-obmc-phosphor-apps-system-mgmt", d)} \
        "

SUMMARY_packagegroup-obmc-phosphor-apps-extras = "Extra features"
RDEPENDS_packagegroup-obmc-phosphor-apps-extras = " \
	obmc-rest \
        host-ipmid \
        "

SUMMARY_packagegroup-obmc-phosphor-apps-extrasdev = "Development features"
RDEPENDS_packagegroup-obmc-phosphor-apps-extrasdev = " \
        rest-dbus \
        "

SUMMARY_packagegroup-obmc-phosphor-apps-fan-mgmt = "Fan management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-fan-mgmt = \
        "${@cf_enabled("obmc-phosphor-fan-mgmt", " \
                virtual/obmc-phosphor-fan-mgmt \
                ${VIRTUAL-RUNTIME_obmc-phosphor-fan-ctl} \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-chassis-mgmt = "Chassis management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-chassis-mgmt = "\
        ${@cf_enabled("obmc-phosphor-chassis-mgmt", " \
                virtual/obmc-phosphor-chassis-mgmt \
                ${VIRTUAL-RUNTIME_obmc-phosphor-chassis-ctl} \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-sensor-mgmt = "Sensor management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-sensor-mgmt = "\
        ${@cf_enabled("obmc-phosphor-sensor-mgmt", " \
                virtual/obmc-phosphor-sensor-mgmt \
                ${VIRTUAL-RUNTIME_obmc-phosphor-sensor-ctl} \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-flash-mgmt = "Flash management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-flash-mgmt = "\
        ${@cf_enabled("obmc-phosphor-flash-mgmt", " \
                virtual/obmc-phosphor-flash-mgmt \
                ${VIRTUAL-RUNTIME_obmc-phosphor-flash-ctl} \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-event-mgmt = "Event management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-event-mgmt = " \
        ${@df_enabled("obmc-phosphor-event-mgmt", " \
                virtual/obmc-phosphor-event-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-policy-mgmt = "Policy management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-policy-mgmt = " \
        ${@df_enabled("obmc-phosphor-policy-mgmt", " \
                virtual/obmc-phosphor-policy-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-user-mgmt = "User management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-user-mgmt = " \
        ${@df_enabled("obmc-phosphor-user-mgmt", " \
                virtual/obmc-phosphor-user-mgmt \
        ", d)}"

SUMMARY_packagegroup-obmc-phosphor-apps-system-mgmt = "System management support"
RDEPENDS_packagegroup-obmc-phosphor-apps-system-mgmt = " \
        ${@df_enabled("obmc-phosphor-system-mgmt", " \
                virtual/obmc-phosphor-system-mgmt \
        ", d)}"
