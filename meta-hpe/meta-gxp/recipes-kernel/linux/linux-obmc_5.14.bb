KBRANCH ?= "dev-5.14-gxp-openbmc"
LINUX_VERSION ?= "5.14.0"
SRCREV="6f1a2c5c0cc59692d5beafe5ff8a4c7539b82d7d"

require linux-obmc.inc
require conf/machine/include/fitimage-sign.inc

# OpenBMC loads in kernel features via other mechanisms so this check
# in the kernel-yocto.bbclass is not required
#KERNEL_DANGLING_FEATURES_WARN_ONLY="1"
