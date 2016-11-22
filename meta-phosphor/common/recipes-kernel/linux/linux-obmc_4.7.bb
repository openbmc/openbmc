KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"

KSRC ="git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"
SRCREV="bd9555036c39fe7efc860495d2203fa5569a3b59"

require linux-obmc.inc
