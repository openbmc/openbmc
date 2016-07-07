SUMMARY = "Phosphor OpenBMC BSP Example Application"
DESCRIPTION = "Phosphor OpenBMC QEMU BSP example implementation."
PR = "r1"

DBUS_SERVICES = " \
        org.openbmc.examples.SDBusService \
        "

S = "${WORKDIR}"
SRC_URI += "file://Makefile \
           file://obmc-phosphor-example-sdbus.c \
           "
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon
