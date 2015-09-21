SUMMARY = "Phosphor OpenBMC BSP Example Application"
DESCRIPTION = "Phosphor OpenBMC QEMU BSP example implementation."
PR = "r1"

DBUS_SERVICES = " \
        org.openbmc.examples.PythonService0 \
        org.openbmc.examples.PythonService1 \
        "

inherit obmc-phosphor-pydbus-service
