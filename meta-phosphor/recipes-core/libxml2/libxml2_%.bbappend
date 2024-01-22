# OpenBMC doesn't use python anymore, so no need for libxml python bindings
PACKAGECONFIG:remove:openbmc-phosphor:class-target = "python"

# Upstream bitbake currently has a bug that requires an explicit
# inherit here.  See https://bugzilla.yoctoproject.org/show_bug.cgi?id=15361
inherit python3targetconfig
