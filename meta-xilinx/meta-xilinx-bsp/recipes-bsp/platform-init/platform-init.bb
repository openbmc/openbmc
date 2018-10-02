SUMMARY = "Xilinx Platform Headers"
DESCRPTION = "Xilinx ps*_init_gpl.c/h platform init code, used for building u-boot-spl and fsbl"
HOMEPAGE = "http://www.xilinx.com"
SECTION = "bsp"

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit xilinx-platform-init

COMPATIBLE_MACHINE = "$^"
COMPATIBLE_MACHINE_picozed-zynq7 = "picozed-zynq7"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PROVIDES += "virtual/xilinx-platform-init"

SRC_URI = "${@" ".join(["file://%s" % f for f in (d.getVar('PLATFORM_INIT_FILES') or "").split()])}"

S = "${WORKDIR}"

SYSROOT_DIRS += "${PLATFORM_INIT_DIR}"

do_compile() {
	:
}

do_install() {
	install -d ${D}${PLATFORM_INIT_DIR}
	for i in ${PLATFORM_INIT_FILES}; do
		install -m 0644 ${S}/$i ${D}${PLATFORM_INIT_DIR}/
	done
}

FILES_${PN} += "${PLATFORM_INIT_DIR}/*"

