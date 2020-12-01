SUMMARY = "DMI (Desktop Management Interface) table related utilities"
HOMEPAGE = "http://www.nongnu.org/dmidecode/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/dmidecode/${BP}.tar.xz \
           file://0001-Committing-changes-from-do_unpack_extra.patch \
           "

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm|powerpc|powerpc64).*-linux"

EXTRA_OEMAKE = "-e MAKEFLAGS="

# The upstream buildsystem uses 'docdir' as the path where it puts AUTHORS,
# README, etc, but we don't want those in the root of our docdir.
docdir .= "/${BPN}"

do_install() {
	oe_runmake DESTDIR="${D}" install
}

SRC_URI[sha256sum] = "82c737a780614c38a783e8055340d295e332fb12c7f418b5d21a0797d3fb1455"

