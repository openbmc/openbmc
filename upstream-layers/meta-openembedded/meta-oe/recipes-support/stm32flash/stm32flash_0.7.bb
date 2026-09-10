SUMMARY = "Open source flash program for STM32 using the ST serial bootloader"
HOMEPAGE = "https://sourceforge.net/projects/stm32flash/"
BUGTRACKER = "https://sourceforge.net/p/stm32flash/tickets/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"

do_install() {
	oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}

SRC_URI[sha256sum] = "c4c9cd8bec79da63b111d15713ef5cc2cd947deca411d35d6e3065e227dc414a"

# downloads.sourceforge.net has no browsable listing, use the SF files page.
# Regex excludes the 0.3beta/0.3beta2 tarballs and the win64 binary zips.
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/stm32flash/files/"
UPSTREAM_CHECK_REGEX = "stm32flash-(?P<pver>\d+(\.\d+)+)\.tar\.gz"
