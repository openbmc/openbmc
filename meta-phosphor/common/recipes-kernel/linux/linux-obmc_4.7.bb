KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"

KSRC ?= "git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"
SRCREV="517d997328ab0d56b1fccb9e58cd5cd71090a8f3"

require linux-obmc.inc
