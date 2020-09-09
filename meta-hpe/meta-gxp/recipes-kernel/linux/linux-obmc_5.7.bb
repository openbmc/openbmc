KBRANCH ?= "dev-5.7-gxp-openbmc"
LINUX_VERSION ?= "5.7.10"

SRCREV="1ca49db2b4baf304d29396a603d0308770797a5c"
require linux-obmc.inc

SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"

# OpenBMC loads in kernel features via other mechanisms so this check
# in the kernel-yocto.bbclass is not required
KERNEL_DANGLING_FEATURES_WARN_ONLY="1"
