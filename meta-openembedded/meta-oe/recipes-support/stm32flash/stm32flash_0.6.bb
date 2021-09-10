SUMMARY = "Open source flash program for STM32 using the ST serial bootloader"
HOMEPAGE = "https://sourceforge.net/projects/stm32flash/"
BUGTRACKER = "https://sourceforge.net/p/stm32flash/tickets/"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"

do_install() {
	oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}

SRC_URI[sha256sum] = "ee9b40d4d3e5cd28b993e08ae2a2c3c559b6bea8730cd7e1d40727dedb1dda09"
