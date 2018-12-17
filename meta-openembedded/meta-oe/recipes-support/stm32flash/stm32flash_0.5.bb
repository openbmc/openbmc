SUMMARY = "Open source flash program for STM32 using the ST serial bootloader"
HOMEPAGE = "https://sourceforge.net/projects/stm32flash/"
BUGTRACKER = "https://sourceforge.net/p/stm32flash/tickets/"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/${BPN}"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"

do_install() {
	oe_runmake install DESTDIR=${D} PREFIX=${prefix}
}

SRC_URI[md5sum] = "40f673502949f3bb655d2bcc539d7b6a"
SRC_URI[sha256sum] = "97aa9422ef02e82f7da9039329e21a437decf972cb3919ad817f70ac9a49e306"
