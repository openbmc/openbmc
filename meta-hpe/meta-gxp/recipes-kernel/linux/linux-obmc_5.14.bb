KBRANCH ?= "dev-5.14-gxp-openbmc"
LINUX_VERSION ?= "5.14.0"
SRCREV="4ff23625306630ce50d9454fca590fa9b2daeb53"

require linux-obmc.inc
require conf/machine/include/fitimage-sign.inc

# OpenBMC loads in kernel features via other mechanisms so this check
# in the kernel-yocto.bbclass is not required
#KERNEL_DANGLING_FEATURES_WARN_ONLY="1"
