LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SUMMARY = "Basic grub.cfg for use in EFI systems"

RPROVIDES_${PN} += "virtual/grub-bootconf"

inherit grub-efi-cfg

S = "${WORKDIR}"

GRUB_CFG = "${S}/grub-bootconf"
LABELS = "boot"

ROOT ?= "root=/dev/sda2"

python do_configure() {
    bb.build.exec_func('build_efi_cfg', d)
}

do_configure[vardeps] += "APPEND ROOT"

do_install() {
	install -d ${D}/boot
	install -d ${D}/boot/EFI
	install -d ${D}/boot/EFI/BOOT
	install grub-bootconf ${D}/boot/EFI/BOOT/grub.cfg
}

FILES_${PN} = "/boot/EFI/BOOT/grub.cfg"
