SUMMARY = "Callback Manager"
DESCRIPTION = "D-Bus daemon that registers matches that trigger method calls"
require s2600wf.inc

S = "${UNPACKDIR}/${BP}/subprojects/${BPN}"

SYSTEMD_SERVICE:${PN} += "callback-manager.service"
