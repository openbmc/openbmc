KBRANCH ?= "dev-4.7-fsi"
LINUX_VERSION ?= "4.7"

KSRC="git://github.com/shenki/linux;protocol=git;branch=${KBRANCH}"
SRCREV="162662cdef0be1f3bfb05b4c7def4289daae2ecc"

require linux-obmc.inc
