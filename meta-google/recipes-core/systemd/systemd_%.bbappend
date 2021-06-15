# We want to be able to manage our coredumps
PACKAGECONFIG_append_gbmc = " coredump"

# Disable timesync as we don't use it and it makes rebooting much slower
PACKAGECONFIG_remove_gbmc = "timesyncd"

# We don't need any legacy sysv rc compatability
PACKAGECONFIG_remove_gbmc = "sysvinit"
