SUMMARY = "DMI (Desktop Management Interface) table related utilities"
HOMEPAGE = "http://www.nongnu.org/dmidecode/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/dmidecode/${BP}.tar.xz"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm|powerpc|powerpc64).*-linux"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
	oe_runmake DESTDIR="${D}" install
}

do_unpack_extra() {
	sed -i -e '/^prefix/s:/usr/local:${exec_prefix}:' ${S}/Makefile
}
addtask unpack_extra after do_unpack before do_patch

SRC_URI[md5sum] = "679c2c015c515aa6ca5f229aee49c102"
SRC_URI[sha256sum] = "d766ce9b25548c59b1e7e930505b4cad9a7bb0b904a1a391fbb604d529781ac0"

