LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SUMMARY = "Basic systemd-boot configuration files"

RPROVIDES_${PN} += "virtual/systemd-bootconf"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit systemd-boot-cfg

S = "${WORKDIR}"

LABELS = "boot"

ROOT ?= "root=/dev/sda2"
APPEND_append = " ${ROOT}"

python do_configure() {
    bb.build.exec_func('build_efi_cfg', d)
}

do_configure[vardeps] += "APPEND"

do_install() {
	install -d ${D}/boot
	install -d ${D}/boot/loader
	install -d ${D}/boot/loader/entries
	install loader.conf ${D}/boot/loader/
	rm loader.conf
	install *.conf ${D}/boot/loader/entries/
}

FILES_${PN} = "/boot/loader/* /boot/loader/entries/*"
