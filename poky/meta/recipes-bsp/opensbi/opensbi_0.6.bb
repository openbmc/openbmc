SUMMARY = "RISC-V Open Source Supervisor Binary Interface (OpenSBI)"
DESCRIPTION = "OpenSBI aims to provide an open-source and extensible implementation of the RISC-V SBI specification for a platform specific firmware (M-mode) and a general purpose OS, hypervisor or bootloader (S-mode or HS-mode). OpenSBI implementation can be easily extended by RISC-V platform or System-on-Chip vendors to fit a particular hadware configuration."
HOMEPAGE = "https://github.com/riscv/opensbi"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=42dd9555eb177f35150cf9aa240b61e5"

require opensbi-payloads.inc

inherit autotools-brokensep deploy

SRCREV = "ac5e821d50be631f26274765a59bc1b444ffd862"
SRC_URI = "git://github.com/riscv/opensbi.git \
           file://0001-Makefile-Don-t-specify-mabi-or-march.patch \
          "

S = "${WORKDIR}/git"

EXTRA_OEMAKE += "PLATFORM=${RISCV_SBI_PLAT} I=${D}"
# If RISCV_SBI_PAYLOAD is set then include it as a payload
EXTRA_OEMAKE_append = " ${@riscv_get_extra_oemake_image(d)}"

# Required if specifying a custom payload
do_compile[depends] += "${@riscv_get_do_compile_depends(d)}"

do_install_append() {
	# In the future these might be required as a dependency for other packages.
	# At the moment just delete them to avoid warnings
	rm -r ${D}/include
	rm -r ${D}/platform/${RISCV_SBI_PLAT}/lib
	rm -r ${D}/platform/${RISCV_SBI_PLAT}/firmware/payloads
	rm -r ${D}/lib
}

do_deploy () {
	install -m 755 ${D}/platform/${RISCV_SBI_PLAT}/firmware/fw_payload.* ${DEPLOYDIR}/
	install -m 755 ${D}/platform/${RISCV_SBI_PLAT}/firmware/fw_jump.* ${DEPLOYDIR}/
	install -m 755 ${D}/platform/${RISCV_SBI_PLAT}/firmware/fw_dynamic.* ${DEPLOYDIR}/
}

addtask deploy before do_build after do_install

FILES_${PN} += "/platform/${RISCV_SBI_PLAT}/firmware/fw_jump.*"
FILES_${PN} += "/platform/${RISCV_SBI_PLAT}/firmware/fw_payload.*"
FILES_${PN} += "/platform/${RISCV_SBI_PLAT}/firmware/fw_dynamic.*"

COMPATIBLE_HOST = "(riscv64|riscv32).*"
INHIBIT_PACKAGE_STRIP = "1"

SECURITY_CFLAGS = ""
