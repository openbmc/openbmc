# Disable timesync as we don't use it and it makes rebooting much slower
PACKAGECONFIG:remove:gbmc = "timesyncd"
