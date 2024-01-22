# OpenBMC doesn't use python anymore, so no need for libxml python bindings
PACKAGECONFIG:remove:openbmc-phosphor:class-target = "python"
