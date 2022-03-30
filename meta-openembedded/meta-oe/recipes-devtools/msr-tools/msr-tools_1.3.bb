SUMMARY = "utilies to read and write Intel model-specific registers"
HOMEPAGE = "https://01.org/msr-tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://Makefile;beginline=1;endline=11;md5=678a3928c856042424c734f4a7586e65"
SECTION = "devel"

SRC_URI = "https://01.org/sites/default/files/downloads/msr-tools/msr-tools-${PV}.zip"
SRC_URI[md5sum] = "67d18527f64fd707c9b7a79f13d19fd5"
SRC_URI[sha256sum] = "f9457b5340f5f274da4239a461f0db6b514142b40461559d9c6150f05aab1d1e"

S = "${WORKDIR}/msr-tools-master"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 rdmsr ${D}${sbindir}
    install -m 0755 wrmsr ${D}${sbindir}
}
