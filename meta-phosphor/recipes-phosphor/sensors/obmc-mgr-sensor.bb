SUMMARY = "OpenBMC sensor manager"
DESCRIPTION = "OpenBMC sensor manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-pygobject\
        pyphosphor \
        pyphosphor-dbus \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pysensormgr"
DBUS_SERVICE_${PN} += "org.openbmc.Sensors.service"
