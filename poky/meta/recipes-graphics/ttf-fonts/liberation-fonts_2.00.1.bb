SUMMARY = "Liberation(tm) Fonts"
DESCRIPTION = "The Liberation(tm) Fonts is a font family originally \
created by Ascender(c) which aims at metric compatibility with \
Arial, Times New Roman, Courier New."
HOMEPAGE = "https://releases.pagure.org/liberation-fonts/"
BUGTRACKER = "https://bugzilla.redhat.com/"

SECTION = "x11/fonts"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f96db970a9a46c5369142b99f530366b"
PE = "1"

inherit allarch fontcache

FONT_PACKAGES = "${PN}"

SRC_URI = "https://releases.pagure.org/liberation-fonts/liberation-fonts-ttf-${PV}.tar.gz \
           file://30-liberation-aliases.conf"

S = "${WORKDIR}/liberation-fonts-ttf-${PV}"

SRC_URI[md5sum] = "5c781723a0d9ed6188960defba8e91cf"
SRC_URI[sha256sum] = "7890278a6cd17873c57d9cd785c2d230d9abdea837e96516019c5885dd271504"

do_install () {
	install -d ${D}${datadir}/fonts/ttf/
	for i in *.ttf; do
		install -m 0644 $i ${D}${prefix}/share/fonts/ttf/${i}
	done

	install -d ${D}${sysconfdir}/fonts/conf.d/
	install -m 0644 ${WORKDIR}/30-liberation-aliases.conf ${D}${sysconfdir}/fonts/conf.d/

	install -d ${D}${prefix}/share/doc/${BPN}/
	install -m 0644 LICENSE ${D}${datadir}/doc/${BPN}/
}

PACKAGES = "${PN}"
FILES_${PN} += "${sysconfdir} ${datadir}"

BBCLASSEXTEND = "native nativesdk"
