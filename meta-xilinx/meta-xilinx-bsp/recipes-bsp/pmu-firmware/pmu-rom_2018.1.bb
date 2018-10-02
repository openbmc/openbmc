SUMMARY = "PMU ROM for QEMU"
DESCRIPTION = "The ZynqMP PMU ROM for QEMU emulation"
HOMEPAGE = "http://www.xilinx.com"
SECTION = "bsp"

# The BSP package does not include any license information.
LICENSE = "Proprietary"
LICENSE_FLAGS = "xilinx"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

COMPATIBLE_MACHINE = "zcu102-zynqmp"

inherit deploy
inherit xilinx-fetch-restricted

BSP_NAME = "xilinx-zcu102"
BSP_FILE = "${BSP_NAME}-v${PV}-final.bsp"
SRC_URI = "https://www.xilinx.com/member/forms/download/xef.html?filename=${BSP_FILE};downloadfilename=${BSP_FILE}"
SRC_URI[md5sum] = "cea5f11761e7f38cbfcf0a07a19094e0"
SRC_URI[sha256sum] = "7ac0ac3a5fb7dd162c0a922c66edb33b5737955ef6570a1a1d3b15b4344f7cc1"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_compile() {
	# Extract the rom into workdir
	tar -xf ${WORKDIR}/${BSP_FILE} ${BSP_NAME}-${PV}/pre-built/linux/images/pmu_rom_qemu_sha3.elf -C ${S}
	# tar preserves the tree, so use find to get the full path and move to to the root
	for i in $(find ${S} -type f -name *.elf); do mv $i ${S}/pmu-rom.elf; done
}

do_install() {
	:
}

do_deploy () {
	install -D ${S}/pmu-rom.elf ${DEPLOYDIR}/pmu-rom.elf
}

addtask deploy before do_build after do_install

