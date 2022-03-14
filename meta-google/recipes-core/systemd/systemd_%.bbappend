# We want to be able to manage our coredumps
PACKAGECONFIG:append:gbmc = " coredump"

# Disable timesync as we don't use it and it makes rebooting much slower
PACKAGECONFIG:remove:gbmc = "timesyncd"

# We don't need any legacy sysv rc compatability
PACKAGECONFIG:remove:gbmc = "sysvinit"

# We don't enable kernel modules
PACKAGECONFIG:remove:gbmc = "kmod"
