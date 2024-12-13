PACKAGECONFIG:append:romulus = " verify_signature"

# New code update doesn't fit in Romulus
PACKAGECONFIG:remove:romulus = "software-update-dbus-interface"
