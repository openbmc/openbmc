SUMMARY = "RISC-V Open Source Supervisor Binary Interface (OpenSBI)"
DESCRIPTION = "OpenSBI aims to provide an open-source and extensible implementation of the RISC-V SBI specification for a platform specific firmware (M-mode) and a general purpose OS, hypervisor or bootloader (S-mode or HS-mode). OpenSBI implementation can be easily extended by RISC-V platform or System-on-Chip vendors to fit a particular hadware configuration."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=c36118b4f615f9da37635f2a7ac8ccaf"
DEPENDS += "dtc-native"

require opensbi-payloads.inc

inherit autotools-brokensep

SRCREV = "ca20ac0cd4c099006d4eea4d9ac7bd7b58e2ae0f"
SRC_URI = "git://github.com/riscv/opensbi.git \
           file://0001-Makefile-Don-t-specify-mabi-or-march.patch \
           file://0002-lib-Create-a-sbi_ipi_data-structure.patch \
          "

S = "${WORKDIR}/git"

SRC_URI[md5sum] = "621f38d8205ef5fb185e4055025e73df"
SRC_URI[sha256sum] = "07f18b73abf3b85aabe5bead19a923716c100d30eb58033459f39c3a224be300"

EXTRA_OEMAKE += "PLATFORM=${RISCV_SBI_PLAT} I=${D}"
# If RISCV_SBI_PAYLOAD is set then include it as a payload
EXTRA_OEMAKE_append = " ${@riscv_get_extra_oemake_image(d)} ${@riscv_get_extra_oemake_fdt(d)}"

# Required if specifying a custom payload
do_compile[depends] += "${@riscv_get_do_compile_depends(d)}"

do_install_append() {
	# In the future these might be required as a dependency for other packages.
	# At the moment just delete them to avoid warnings
	rm -r ${D}/include
	rm -r ${D}/platform/${RISCV_SBI_PLAT}/lib
	rm -r ${D}/platform/${RISCV_SBI_PLAT}/firmware/payloads
}

do_deploy () {
	install -d ${DEPLOY_DIR_IMAGE}
	install -m 755 ${D}/platform/${RISCV_SBI_PLAT}/firmware/fw_payload.* ${DEPLOY_DIR_IMAGE}/
	install -m 755 ${D}/platform/${RISCV_SBI_PLAT}/firmware/fw_jump.* ${DEPLOY_DIR_IMAGE}/
}

addtask deploy after do_install

FILES_${PN} += "/platform/${RISCV_SBI_PLAT}/firmware/fw_jump.*"
FILES_${PN} += "/platform/${RISCV_SBI_PLAT}/firmware/fw_payload.*"

COMPATIBLE_HOST = "(riscv64|riscv32).*"
INHIBIT_PACKAGE_STRIP = "1"
