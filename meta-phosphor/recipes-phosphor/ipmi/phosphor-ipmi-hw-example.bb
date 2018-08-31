SUMMARY = "Phosphor OpenBMC host IPMI to DBUS Example"
DESCRIPTION = "Phosphor OpenBMC host IPMI to DBUS example implementation."
PR = "r1"

RDEPENDS_${PN} += "python-subprocess python-dbus python-pygobject"
RRECOMMENDS_${PN} += "phosphor-ipmi-host"

inherit skeleton-python

PROVIDES += "virtual/obmc-host-ipmi-hw"
RPROVIDES_${PN} += "virtual-obmc-host-ipmi-hw"

SKELETON_DIR = "pyipmitest"
