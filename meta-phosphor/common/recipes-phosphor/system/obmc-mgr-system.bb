SUMMARY = "OpenBMC system manager"
DESCRIPTION = "OpenBMC system manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-dbus-service

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-subprocess \
        python-pygobject \
        pyphosphor-dbus \
        ${VIRTUAL-RUNTIME_skeleton_workbook} \
        "

SKELETON_DIR = "pysystemmgr"

DBUS_SERVICE_${PN} += "org.openbmc.managers.System.service"
