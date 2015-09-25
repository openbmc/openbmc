SUMMARY = "Phosphor OpenBMC DBUS Permissions"
DESCRIPTION = "Phosphor OpenBMC DBUS Permissions."
HOMEPAGE = "http://github.com/openbmc/"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

DBUS_SERVICES += " \
        org.openbmc \
        "
