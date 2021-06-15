SUMMARY = "Liberation(tm) Fonts"
DESCRIPTION = "The Liberation(tm) Fonts is a font family originally \
created by Ascender(c) which aims at metric compatibility with \
Arial, Times New Roman, Courier New."
HOMEPAGE = "https://github.com/liberationfonts/liberation-fonts"
BUGTRACKER = "https://bugzilla.redhat.com/"

SECTION = "x11/fonts"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f96db970a9a46c5369142b99f530366b"
PE = "1"

SRC_URI = "https://github.com/liberationfonts/liberation-fonts/files/6418984/liberation-fonts-ttf-${PV}.tar.gz \
           file://30-liberation-aliases.conf"
SRC_URI[sha256sum] = "26f85412dd0aa9d061504a1cc8aaf0aa12a70710e8d47d8b65a1251757c1a5ef"
UPSTREAM_CHECK_URI = "https://github.com/liberationfonts/liberation-fonts/releases"

S = "${WORKDIR}/liberation-fonts-ttf-${PV}"

inherit allarch fontcache

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
