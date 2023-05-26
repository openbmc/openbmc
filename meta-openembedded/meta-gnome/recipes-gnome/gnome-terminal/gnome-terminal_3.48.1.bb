SUMMARY = "GNOME terminal"
LICENSE = "GPL-3.0-only & GFDL-1.3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
    file://COPYING.GFDL;md5=a22d0be1ce2284b67950a4d1673dd1b0 \
"

GNOMEBASEBUILDCLASS = "meson"

inherit pkgconfig meson gsettings gnome-help gettext itstool upstream-version-is-even

DEPENDS = " \
    glib-2.0 \
    docbook-xsl-stylesheets-native libxslt-native \
    desktop-file-utils-native \
    gtk+3 \
    gsettings-desktop-schemas \
    vte \
    dconf \
    libpcre2 \
"

SRC_URI = "git://gitlab.gnome.org/GNOME/gnome-terminal.git;protocol=https;nobranch=1 \
           file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch \
           "
SRCREV = "1446ff0abc478bdd03c6665b718214d075729024"
S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[nautilus] = "-Dnautilus_extension=true,-Dnautilus_extension=false,nautilus,nautilus"
PACKAGECONFIG[search_provider] = "-Dsearch_provider=true,-Dsearch_provider=false,,gnome-shell"

FILES:${PN} += " \
    ${datadir} \
    ${libdir}/nautilus/extensions-4 \
    ${systemd_user_unitdir} \
"

RRECOMMENDS:${PN} += "vte-prompt gsettings-desktop-schemas"
