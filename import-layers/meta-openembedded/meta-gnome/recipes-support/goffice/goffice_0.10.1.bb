DESCRIPTION="Gnome Office Library"

LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6dc33ff21e1ba1ac1a2a1069d361e29e"

DEPENDS = "libxml-parser-perl-native glib-2.0 gtk+3 pango cairo libgsf libpcre libxml2 libart-lgpl librsvg intltool"

inherit gnomebase pkgconfig perlnative gobject-introspection

GNOME_COMPRESS_TYPE = "xz"

SRC_URI += "file://0001-configure.ac-fix-paths-to-introspection-tools.patch"

SRC_URI[archive.md5sum] = "90fd17c6fe205b779571e00d9b0b4727"
SRC_URI[archive.sha256sum] = "5c38f4e81e874cc8e89481b080f77c47c72bfd6fe2526f4fc2ef87c17f96cad0"

FILES_${PN}-dbg += "${libdir}/goffice/${PV}/plugins/*/.debug"

RRECOMMENDS_${PN} = " \
    goffice-plugin-plot-barcol \
    goffice-plugin-plot-distrib \
    goffice-plugin-plot-pie \
    goffice-plugin-plot-radar \
    goffice-plugin-plot-surface \
    goffice-plugin-plot-xy \
    goffice-plugin-reg-linear \
    goffice-plugin-reg-logfit \
    goffice-plugin-smoothing \
"

FILES_${PN} = "${bindir}/* ${sbindir}/* ${libexecdir}/* ${libdir}/lib*${SOLIBS} \
    ${sysconfdir} ${sharedstatedir} ${localstatedir} \
    ${base_bindir}/* ${base_sbindir}/* \
    ${base_libdir}/*${SOLIBS} \
    ${datadir}/${PN} \
    ${datadir}/pixmaps ${datadir}/applications \
    ${datadir}/idl ${datadir}/omf ${datadir}/sounds \
    ${libdir}/bonobo/servers"

FILES_${PN}-locale = "${libdir}/locale"

PACKAGES_DYNAMIC += "^goffice-plugin-.*"

python populate_packages_prepend () {
    goffice_libdir = d.expand('${libdir}/goffice/${PV}/plugins/')

    do_split_packages(d, goffice_libdir, '(.*)', 'goffice-plugin-%s', 'Goffice plugin for %s', allow_dirs=True)
}

