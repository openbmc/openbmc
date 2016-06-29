KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"
SRCREV="7e7cc303ea151ef23348b1df5662a2d296c62616"
KSRC = "git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"

require common/recipes-kernel/linux/linux-obmc.inc
