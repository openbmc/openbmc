UBOOT_VERSION = "v2019.01"
XILINX_RELEASE_VERSION = "v2019.1"

UBRANCH ?= "master"

SRCREV ?= "d895ac5e94815d4b45dcf09d4752c5c2334a51db"

include u-boot-xlnx.inc
include u-boot-spl-zynq-init.inc

SRC_URI_append_kc705-microblazeel = " file://microblaze-kc705-Convert-microblaze-generic-to-k.patch"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README;beginline=1;endline=4;md5=744e7e3bb0c94b4b9f6b3db3bf893897"

# u-boot-xlnx has support for these
HAS_PLATFORM_INIT ?= " \
		zynq_microzed_config \
		zynq_zed_config \
		zynq_zc702_config \
		zynq_zc706_config \
		zynq_zybo_config \
		xilinx_zynqmp_zcu102_rev1_0_config \
		xilinx_zynqmp_zcu106_revA_config \
		xilinx_zynqmp_zcu104_revC_config \
		xilinx_zynqmp_zcu100_revC_config \
		xilinx_zynqmp_zcu111_revA_config \
		xilinx_zynqmp_zc1275_revA_config \
		xilinx_zynqmp_zc1275_revB_config \
		xilinx_zynqmp_zc1254_revA_config \
		"

