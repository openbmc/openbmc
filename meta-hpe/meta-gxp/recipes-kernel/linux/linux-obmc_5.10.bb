KBRANCH ?= "dev-5.10-gxp-openbmc"
LINUX_VERSION ?= "5.10.17"
SRCREV="8d942b456304d3a21597cda47387a45ace61a225"

require linux-obmc.inc
require conf/machine/include/fitimage-sign.inc

# OpenBMC loads in kernel features via other mechanisms so this check
# in the kernel-yocto.bbclass is not required
#KERNEL_DANGLING_FEATURES_WARN_ONLY="1"
