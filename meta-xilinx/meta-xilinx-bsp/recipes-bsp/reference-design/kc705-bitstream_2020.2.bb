SUMMARY = "KC705 Pre-built Bitstream"
DESCRIPTION = "A Pre-built bitstream for the KC705, which is capable of booting a Linux system."
HOMEPAGE = "http://www.xilinx.com"
SECTION = "bsp"

# The BSP package does not include any license information.
LICENSE = "Proprietary"
LICENSE_FLAGS = "xilinx"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

COMPATIBLE_MACHINE = "kc705-microblazeel"

inherit deploy
inherit xilinx-fetch-restricted

BSP_NAME = "xilinx-kc705"
BSP_FILE = "${BSP_NAME}-v${PV}-final.bsp"
SRC_URI = "https://www.xilinx.com/member/forms/download/xef.html?filename=${BSP_FILE};downloadfilename=${BSP_FILE}"
SRC_URI[md5sum] = "5c0365a8a26cc27b4419aa1d7dd82351"
SRC_URI[sha256sum] = "a909a91a37a9925ee2f972ccb10f986a26ff9785c1a71a483545a192783bf773"

PROVIDES = "virtual/bitstream"

FILES_${PN} += "/boot/download.bit"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

# deps needed to extract content from the .bsp file
DEPENDS += "tar-native gzip-native"

do_compile() {
	# Extract the bitstream into workdir
	tar -xf ${WORKDIR}/${BSP_FILE} ${BSP_NAME}-axi-full-${PV}/pre-built/linux/images/download.bit -C ${S}
	# move the bit file to ${S}/ as it is in a subdir in the tar file
	for i in $(find -type f -name download.bit); do mv $i ${S}; done
}

do_install() {
	install -D ${S}/download.bit ${D}/boot/download.bit
}

do_deploy () {
	install -D ${S}/download.bit ${DEPLOYDIR}/download.bit
}

addtask deploy before do_build after do_install

