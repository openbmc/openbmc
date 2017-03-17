KBRANCH ?= "dev-4.7"
LINUX_VERSION ?= "4.7.10"

SRCREV="c72ced5a19ceae19c7d792ae3e982bee5607dab4"
SRC_URI += "file://v3-0001-drivers-fsi-Increase-delay-before-sampling-input-.patch"

require linux-obmc.inc
