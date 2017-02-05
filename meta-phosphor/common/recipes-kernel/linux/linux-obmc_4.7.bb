KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"

KSRC ?= "git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"
SRCREV="3f8752b1cfbd84678740c7436fef20bfbbb74a7c"

require linux-obmc.inc
