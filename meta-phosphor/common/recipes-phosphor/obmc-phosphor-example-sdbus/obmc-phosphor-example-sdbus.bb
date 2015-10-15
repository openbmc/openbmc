SUMMARY = "Phosphor OpenBMC BSP Example Application"
DESCRIPTION = "Phosphor OpenBMC QEMU BSP example implementation."
PR = "r1"

DBUS_SERVICES = " \
        org.openbmc.examples.SDBusService0 \
        org.openbmc.examples.SDBusService1 \
        "

inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon
