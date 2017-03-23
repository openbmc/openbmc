KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7.10"

KBRANCH = "i2c-aspeed-hardening"
KSRC = "git://github.com/amboar/linux;protocol=git;branch=${KBRANCH}"
SRCREV = "9c32dd8619ca44a898626796e9f8f489cd1ca58b"

require linux-obmc.inc
