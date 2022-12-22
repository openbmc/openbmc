# Enable Redfish BMC Journal support
EXTRA_OEMESON:append:scm-npcm845  = " -Dredfish-bmc-journal=enabled"

# Enable Redfish Journal support
EXTRA_OEMESON:append:scm-npcm845 = " -Dredfish-bmc-journal=enabled"

# Enable TFTP
EXTRA_OEMESON:append:scm-npcm845  = " -Dinsecure-tftp-update=enabled"

# Increase body limit for FW size
EXTRA_OEMESON:append:scm-npcm845  = " -Dhttp-body-limit=65"

# Enable dbus rest API /xyz/
EXTRA_OEMESON:append:scm-npcm845 = " -Drest=enabled"

# Enable http support for Event Service
EXTRA_OEMESON:append:scm-npcm845 = " -Dinsecure-push-style-notification=enabled"
