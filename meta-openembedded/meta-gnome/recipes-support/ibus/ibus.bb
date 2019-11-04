require ${BPN}.inc

DEPENDS += " \
    ${BPN}-native \
    glib-2.0-native \
    glib-2.0 \
    dbus \
    iso-codes \
"

inherit gtk-icon-cache bash-completion

# for unicode-ucd
EXTRA_OECONF += "--with-ucd-dir=${STAGING_DATADIR}/unicode/ucd"

PACKAGECONFIG ??= " \
    dconf vala \
    ${@bb.utils.contains_any('DISTRO_FEATURES', [ 'wayland', 'x11' ], 'gtk3', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)} \
"

do_configure_prepend() {
    # run native unicode-parser
    sed -i 's:$(builddir)/unicode-parser:unicode-parser:g' ${S}/src/Makefile.am
}

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/GConf \
    ${datadir}/glib-2.0 \
    ${libdir}/gtk-3.0 \
"

FILES_${PN}-dev += " \
    ${datadir}/gettext \
"

