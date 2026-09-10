require ttf.inc

SUMMARY = "Thai Linux Working Group Fonts"
HOMEPAGE = "http://linux.thai.net/projects/fonts-tlwg"
LICENSE = "GPL-2.0-only AND LicenseRef-TLWG"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/fonts-tlwg-${PV}/COPYING;md5=166297e03f25f65003b290136b7287f7"

SRC_URI = "http://linux.thai.net/pub/ThaiLinux/software/fonts-tlwg/fonts-tlwg-${PV}.tar.xz;name=source \
           http://linux.thai.net/pub/ThaiLinux/software/fonts-tlwg/fonts/ttf-tlwg-${PV}.tar.xz;name=ttf"
SRC_URI[source.md5sum] = "dc05ef81ce6b9dd5edc87f83065dbbf4"
SRC_URI[source.sha256sum] = "2531d3e02de1d293e4a9f8527ac4d3d044a845f49dc2d314c5766b4f68730735"
SRC_URI[ttf.md5sum] = "621c0ecf4f7338e2390d3cd0deb00c00"
SRC_URI[ttf.sha256sum] = "e1afa592660a48234051a8c6b54aff36b85db8da0299878ed4d1668a2bda3c57"

do_install:append () {
    install -d ${D}${sysconfdir}/fonts/conf.d

    for x in ${UNPACKDIR}/fonts-tlwg-${PV}/fontconfig/*.conf; do
        install -m 0644 $x ${D}${sysconfdir}/fonts/conf.d/
    done
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/fonts ${sysconfdir}"
