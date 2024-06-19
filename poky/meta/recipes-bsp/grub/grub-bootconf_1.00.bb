LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SUMMARY = "Basic grub.cfg for use in EFI systems"
DESCRIPTION = "Grub might require different configuration file for \
different machines."
HOMEPAGE = "https://www.gnu.org/software/grub/manual/grub/grub.html#Configuration"

RPROVIDES:${PN} += "virtual-grub-bootconf"

inherit grub-efi-cfg

require conf/image-uefi.conf

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

GRUB_CFG = "${S}/grub-bootconf"
LABELS = "boot"

ROOT ?= "root=/dev/sda2"

python do_configure() {
    bb.build.exec_func('build_efi_cfg', d)
}

do_configure[vardeps] += "APPEND ROOT"

do_install() {
	install -d ${D}${EFI_FILES_PATH}
	install grub-bootconf ${D}${EFI_FILES_PATH}/grub.cfg
}

FILES:${PN} = "${EFI_FILES_PATH}/grub.cfg"
