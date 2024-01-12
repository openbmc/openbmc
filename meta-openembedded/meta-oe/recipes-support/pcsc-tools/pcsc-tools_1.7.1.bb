SUMMARY = "Some tools to be used with smart cards and PC/SC"
HOMEPAGE = "http://ludovic.rousseau.free.fr/softwares/pcsc-tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENCE;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/LudovicRousseau/pcsc-tools;protocol=https;branch=master"

SRCREV = "53834cd33155ae1f0332102b1baca6ce63660fc5"

inherit autotools pkgconfig

S = "${WORKDIR}/git"

DEPENDS = "pcsc-lite autoconf-archive-native"

RDEPENDS:${PN} += " \
	${@bb.utils.contains('DISTRO_FEATURES','systemd','pcsc-lite-systemd', 'pcsc-lite', d)} \
	perl \
	perl-module-getopt-std \
	perl-module-file-stat \
	libpcsc-perl \
"

FILES:${PN} += "${datadir}/pcsc/smartcard_list.txt \
		${datadir}/pcsc/gscriptor.png"
