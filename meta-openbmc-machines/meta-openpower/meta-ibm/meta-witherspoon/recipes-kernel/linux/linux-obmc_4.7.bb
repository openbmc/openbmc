KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"
SRCREV="05543e532c78185ba82057415f96d13e5f500072"
KSRC = "git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"

require common/recipes-kernel/linux/linux-obmc.inc
