KBRANCH ?= "cronus.1"
LINUX_VERSION ?= "4.7"

KSRC="git://github.com/cbostic/linux;protocol=git;branch=${KBRANCH}"
SRCREV="24e6afcec50859a365bca5421577cae3df547cb6"

require linux-obmc.inc
