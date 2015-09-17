SUMMARY = "Phosphor OpenBMC BSP Example Application"
DESCRIPTION = "Phosphor OpenBMC QEMU BSP example implementation."
PR = "r1"

inherit obmc-phosphor-py-daemon

DBUS_SERVICES = " \
        org.openbmc.examples.services.Service0 \
        org.openbmc.examples.services.Service1 \
        "

inherit obmc-phosphor-dbus-service
