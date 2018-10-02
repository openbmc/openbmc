# This recipe allows for a 'bleeding edge' u-boot-xlnx build.
# Since this tree is frequently updated, AUTOREV is used to track its contents.
#
# To enable this recipe, set the following in your machine or local.conf
#   PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-xlnx-dev"

UBRANCH ?= "master"

include u-boot-xlnx.inc
include u-boot-spl-zynq-init.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README;beginline=1;endline=6;md5=157ab8408beab40cd8ce1dc69f702a6c"

SRCREV_DEFAULT = "aebea9d20a5aa32857f320c07ca8f9fd1b3dec1f"
SRCREV ?= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/bootloader", "u-boot-xlnx-dev", "${AUTOREV}", "${SRCREV_DEFAULT}", d)}"

PV = "${UBRANCH}-xilinx-dev+git${SRCPV}"

# Newer versions of u-boot have support for these
HAS_PLATFORM_INIT ?= " \
		zynq_microzed_config \
		zynq_zed_config \
		zynq_zc702_config \
		zynq_zc706_config \
		zynq_zybo_config \
		"

