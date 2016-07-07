SUMMARY = "Phosphor OpenBMC BSP Example Application"
DESCRIPTION = "Phosphor OpenBMC QEMU BSP example implementation."
PR = "r1"

DBUS_SERVICES = " \
        org.openbmc.examples.PythonService \
        "

inherit obmc-phosphor-pydbus-service

client = "pyclient-sample"
SRC_URI += "file://${client}.py"
SRC_URI += "file://${PN}.py"
S = "${WORKDIR}"

do_install_append() {
        install -d ${D}${bindir}
        install -m 0755 ${S}/${client}.py ${D}${bindir}/${client}
}
