inherit entity-utils

# Enable Redfish BMC Journal support
EXTRA_OEMESON:append:scm-npcm845  = " -Dredfish-bmc-journal=enabled"

# Enable Redfish DBUS log/Journal support
EXTRA_OEMESON:append:scm-npcm845 = " ${@entity_enabled(d, '-Dredfish-bmc-journal=enabled', '-Dredfish-dbus-log=enabled')}"

# Enable TFTP
EXTRA_OEMESON:append:scm-npcm845  = " -Dinsecure-tftp-update=enabled"

# Increase body limit for FW size
EXTRA_OEMESON:append:scm-npcm845  = " -Dhttp-body-limit=65"

# Enable dbus rest API /xyz/
EXTRA_OEMESON:append:scm-npcm845 = " -Drest=enabled"
