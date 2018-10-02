# This recipe tracks the 'bleeding edge' linux-xlnx repository.
# Since this tree is frequently updated, AUTOREV is used to track its contents.
#
# To enable this recipe, set PREFERRED_PROVIDER_virtual/kernel = "linux-xlnx-dev"

KBRANCH ?= "master"

# Use the SRCREV for the last tagged revision of linux-xlnx.
SRCREV ?= '${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-xlnx-dev", "${AUTOREV}", "84fb0cc65aae5970471cbc54b0c89009b9b904af", d)}'

# skip version sanity, because the version moves with AUTOREV
KERNEL_VERSION_SANITY_SKIP = "1"

LINUX_VERSION ?= "4.9+"
LINUX_VERSION_EXTENSION ?= "-xilinx-dev"

include linux-xlnx.inc

