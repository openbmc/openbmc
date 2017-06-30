SUMMARY = "OpenBMC Syslog settings manager"
DESCRIPTION = "Daemon to manage syslogd settings."
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-py-daemon

DBUS_SERVICE_${PN} += "org.openbmc.SyslogManagement.service"

PROVIDES += "obmc-syslog-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://syslogmgmt.py"

SCRIPT_NAME = "syslogmgmt.py"
INSTALL_NAME = "${SCRIPT_NAME}"
