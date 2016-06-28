SUMMARY = "Phosphor OpenBMC host IPMI to DBUS Example"
DESCRIPTION = "Phosphor OpenBMC host IPMI to DBUS example implementation."
PR = "r1"

RDEPENDS_${PN} += "python-subprocess python-dbus python-pygobject"

SYSTEMD_SERVICE_${PN} = "host-ipmi-hw.service"

inherit obmc-phosphor-host-ipmi-hw
inherit skeleton-python

SKELETON_DIR = "pyipmitest"
