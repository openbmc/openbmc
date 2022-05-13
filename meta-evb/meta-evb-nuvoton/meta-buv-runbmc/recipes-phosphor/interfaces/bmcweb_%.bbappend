FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"


# Enable CPU Log and Raw PECI support
#EXTRA_OEMESON:append:buv-runbmc = " -Dredfish-cpu-log=enabled"
#EXTRA_OEMESON:append:buv-runbmc = " -Dredfish-raw-peci=enabled"

# Enable Redfish BMC Journal support
EXTRA_OEMESON:append:buv-runbmc = " -Dredfish-bmc-journal=enabled"

# Enable DBUS log service
# EXTRA_OEMESON:append:buv-runbmc = " -Dredfish-dbus-log=enabled"

# Enable TFTP
EXTRA_OEMESON:append:buv-runbmc = " -Dinsecure-tftp-update=enabled"

# Increase body limit for BIOS FW
EXTRA_OEMESON:append:buv-runbmc = " -Dhttp-body-limit=40"

# enable debug
# EXTRA_OEMESON:append:buv-runbmc = " -Dbmcweb-logging=enabled"

# Enable dbus rest API /xyz/
EXTRA_OEMESON:append:buv-runbmc = " -Drest=enabled"
