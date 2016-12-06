KBRANCH ?= "cronus.1"
LINUX_VERSION ?= "4.7"

KSRC="git://github.com/cbostic/linux;protocol=git;branch=${KBRANCH}"
SRCREV="9ad8a296b1a02f3b0e5e5050cede5b25c01549e4"

require linux-obmc.inc
