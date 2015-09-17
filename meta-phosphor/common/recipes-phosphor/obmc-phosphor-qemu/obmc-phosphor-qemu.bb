SUMMARY = "Phosphor OpenBMC BSP Example Application"
DESCRIPTION = "Phosphor OpenBMC QEMU BSP example implementation."
PR = "r1"

inherit obmc-phosphor-py-daemon

DBUS_SERVICES = " \
        org.openbmc.examples.services.service0 \
        org.openbmc.examples.services.service1 \
        "

inherit obmc-phosphor-dbus-service
