KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"
SRCREV="b172f8b68d85450a12d4348ee4a006ef8844fe95"
KSRC = "git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"

require common/recipes-kernel/linux/linux-obmc.inc
