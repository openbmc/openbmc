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
    ${@bb.utils.contains_any('DISTRO_FEATURES', [ 'wayland', 'x11' ], 'gtk3 gtk4', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd wayland x11', d)} \
"

do_configure:prepend() {
    # run native unicode-parser
    sed -i 's:$(builddir)/unicode-parser:unicode-parser:g' ${S}/src/Makefile.am
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/GConf \
    ${datadir}/glib-2.0 \
    ${libdir}/gtk-3.0 \
    ${libdir}/gtk-4.0 \
    ${systemd_user_unitdir} \
"

FILES:${PN}-dev += " \
    ${datadir}/gettext \
"

