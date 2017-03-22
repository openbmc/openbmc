SUMMARY = "GTK+ icon theme"
HOMEPAGE = "http://ftp.gnome.org/pub/GNOME/sources/adwaita-icon-theme/"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome"

LICENSE = "LGPL-3.0 | CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c84cac88e46fc07647ea07e6c24eeb7c"

inherit allarch autotools pkgconfig gettext gtk-icon-cache upstream-version-is-even


MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"
SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
          "

SRC_URI[md5sum] = "411be2bd68dd8b0a3c86aca2eb351ce4"
SRC_URI[sha256sum] = "7a0a887349f340dd644032f89d81264b694c4b006bd51af1c2c368d431e7ae35"

do_install_append() {
	# Build uses gtk-encode-symbolic-svg to create png versions:
        # no need to store the svgs anymore.
	rm -f ${D}${prefix}/share/icons/Adwaita/scalable/*/*-symbolic.svg \
	      ${D}${prefix}/share/icons/Adwaita/scalable/*/*-symbolic-rtl.svg
}

PACKAGES = "${PN}-cursors ${PN}-symbolic-hires ${PN}-symbolic ${PN}-hires ${PN}"

RREPLACES_${PN} = "gnome-icon-theme"
RCONFLICTS_${PN} = "gnome-icon-theme"
RPROVIDES_${PN} = "gnome-icon-theme"

FILES_${PN}-cursors = "${prefix}/share/icons/Adwaita/cursors/"
FILES_${PN}-symbolic-hires = "${prefix}/share/icons/Adwaita/96x96/*/*.symbolic.png \
                              ${prefix}/share/icons/Adwaita/64x64/*/*.symbolic.png \
                              ${prefix}/share/icons/Adwaita/48x48/*/*.symbolic.png \
                              ${prefix}/share/icons/Adwaita/32x32/*/*.symbolic.png"
FILES_${PN}-symbolic = "${prefix}/share/icons/Adwaita/16x16/*/*.symbolic.png \
                        ${prefix}/share/icons/Adwaita/24x24/*/*.symbolic.png"
FILES_${PN}-hires = "${prefix}/share/icons/Adwaita/256x256/"
FILES_${PN} = "${prefix}/share/icons/Adwaita/ \
               ${prefix}/share/pkgconfig/adwaita-icon-theme.pc"
