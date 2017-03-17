KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7.10"

SRCREV="81fc37f5415e893247f4c515fcffbff3f40a4059"
SRC_URI += "file://v3-0001-drivers-fsi-Increase-delay-before-sampling-input-.patch"

require linux-obmc.inc
