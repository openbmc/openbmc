require ttf.inc

SUMMARY = "Thai Linux Working Group Fonts"
HOMEPAGE = "http://linux.thai.net/projects/fonts-tlwg"
LICENSE = "GPL-2.0-only & TLWG"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/fonts-tlwg-${PV}/COPYING;md5=3d20cd7eadf4afd5460c0adb32e387fd"

SRC_URI = "http://linux.thai.net/pub/ThaiLinux/software/fonts-tlwg/fonts-tlwg-${PV}.tar.xz;name=source \
           http://linux.thai.net/pub/ThaiLinux/software/ttf-tlwg/ttf-tlwg-${PV}.tar.xz;name=ttf"
SRC_URI[source.md5sum] = "5ea5bc964d992df0428f2e0b85f48400"
SRC_URI[source.sha256sum] = "77fb9832221fde60c9f683ac3fdce7d45ab6e9c0d83df83da969a3fe9faba537"
SRC_URI[ttf.md5sum] = "1bc51f45a7b661404a944fab6911261c"
SRC_URI[ttf.sha256sum] = "aa9cd68969b6f704df3e5b1a2e11204c47f118c8ab052f04c111bd5f299f77c8"

do_install:append () {
    install -d ${D}${sysconfdir}/fonts/conf.d

    for x in ${S}/etc/fonts/conf.avail/*.conf; do
        install -m 0644 $x ${D}${sysconfdir}/fonts/conf.d/
    done
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/fonts ${sysconfdir}"
