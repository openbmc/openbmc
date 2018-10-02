SUMMARY = "The reference design for zybo-linux-bd"
DESCRIPTION = "Contains the Reference Design Files and hardware software \
hand-off file. The HDF provides bitstream and Xilinx ps7_init_gpl.c/h \
platform headers."
SECTION = "bsp"

DEPENDS += "unzip-native"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://Projects/${HW_BD}/readme.txt;md5=e1cb7639bf00b6e730ff3a7f13714951"

COMPATIBLE_MACHINE = "zybo-linux-bd-zynq7"

HW_BD = "linux_bd"

SRC_URI = "git://github.com/Digilent/ZYBO.git;protocol=https;nobranch=1"
SRCREV = "63ca49fe027da49f3b0ac636bd404fd31fbbd945"

PV = "+git${SRCPV}"

S = "${WORKDIR}/git"

HDF = "/Projects/${HW_BD}/hw_handoff/${HW_BD}_wrapper.hdf"

S ?= "${WORKDIR}/${MACHINE}"

PROVIDES = "virtual/bitstream virtual/xilinx-platform-init"

FILES_${PN}-platform-init += "${PLATFORM_INIT_DIR}/*"

FILES_${PN}-bitstream += " \
		download.bit \
		"

PACKAGES = "${PN}-platform-init ${PN}-bitstream"

BITSTREAM ?= "bitstream-${PV}-${PR}.bit"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit xilinx-platform-init
inherit deploy

SYSROOT_DIRS += "${PLATFORM_INIT_DIR}"

do_install() {
	fn=$(unzip -l ${S}/${HDF} | awk '{print $NF}' | grep ".bit$")
	unzip -o ${S}/${HDF} ${fn} -d ${D}
	[ "${fn}" == "download.bit" ] || mv ${D}/${fn} ${D}/download.bit

	install -d ${D}${PLATFORM_INIT_DIR}
	for fn in ${PLATFORM_INIT_FILES}; do
		unzip -o ${S}/${HDF} ${fn} -d ${D}${PLATFORM_INIT_DIR}
	done
}

do_deploy () {
	if [ -e ${D}/download.bit ]; then
		install -d ${DEPLOYDIR}
		install -m 0644 ${D}/download.bit ${DEPLOYDIR}/${BITSTREAM}
		ln -sf ${BITSTREAM} ${DEPLOYDIR}/download.bit
		# for u-boot 2016.3 with spl load bitstream patch
		ln -sf ${BITSTREAM} ${DEPLOYDIR}/bitstream
	fi
}
addtask deploy before do_build after do_install
