KBRANCH ?= "cronus.1"
LINUX_VERSION ?= "4.7"

KSRC="git://github.com/cbostic/linux;protocol=git;branch=${KBRANCH}"
SRCREV="1a67b1573ecc6c7d2cba30b56da54b0f73db731c"

require linux-obmc.inc
