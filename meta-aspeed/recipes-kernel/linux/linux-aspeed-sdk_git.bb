KBRANCH ?= "aspeed-master-v5.1"
KSRC ?= "git://github.com/AspeedTech-BMC/linux;protocol=git;branch=${KBRANCH}"
LINUX_VERSION ?= "5.1.3"

SRCREV="466b2520fa3bbedad4da77d3eaad33d3f8838b7f"

require linux-aspeed.inc

# This must come after the require line
SRC_URI += "file://0001-aspeed-espi-Clean-up-error-handling.patch"
