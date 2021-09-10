# Add checkstop monitor as part of host state management package
# This will kick start a gpio monitor that will catch the
# host checkstop conditions and takes necessary actions
RDEPENDS:${PN}-host-state-mgmt:append:df-openpower = " checkstop-monitor"

# Add openpower debug collector as a requirement for state-mgmt
# since it is used during checkstop handling.
RDEPENDS:${PN}-host-state-mgmt:append:df-openpower = " openpower-debug-collector"
