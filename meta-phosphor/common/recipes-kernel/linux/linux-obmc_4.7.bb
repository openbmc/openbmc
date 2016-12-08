KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7"

KSRC ?= "git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"
SRCREV="120956cab11d86fa801d53811ed2f7f844216a2f"

require linux-obmc.inc
