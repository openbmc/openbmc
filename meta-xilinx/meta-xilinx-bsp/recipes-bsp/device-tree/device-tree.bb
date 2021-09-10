SUMMARY = "Xilinx BSP device trees"
DESCRIPTION = "Xilinx BSP device trees from within layer."
SECTION = "bsp"

# the device trees from within the layer are licensed as MIT, kernel includes are GPL
LICENSE = "MIT & GPLv2"
LIC_FILES_CHKSUM = " \
		file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
		file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
		"

inherit devicetree

DEPENDS += "python3-dtc-native"

PROVIDES = "virtual/dtb"

# common zynq include
SRC_URI_append_zynq = " file://zynq-7000-qspi-dummy.dtsi"

# device tree sources for the various machines
COMPATIBLE_MACHINE_picozed-zynq7 = ".*"
SRC_URI_append_picozed-zynq7 = " file://picozed-zynq7.dts"

COMPATIBLE_MACHINE_qemu-zynq7 = ".*"
SRC_URI_append_qemu-zynq7 = " file://qemu-zynq7.dts"

COMPATIBLE_MACHINE_zybo-linux-bd-zynq7 = ".*"
SRC_URI_append_zybo-linux-bd-zynq7 = " \
		file://zybo-linux-bd-zynq7.dts \
		file://pcw.dtsi \
		file://pl.dtsi \
		"

COMPATIBLE_MACHINE_kc705-microblazeel = ".*"
SRC_URI_append_kc705-microblazeel = " \
		file://kc705-microblazeel.dts \
		file://pl.dtsi \
		file://system-conf.dtsi \
		"

