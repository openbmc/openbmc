DESCRIPTION = "A multilingual user input method library"
HOMEPAGE = "http://uim.freedesktop.org/"
LICENSE = "BSD-3-Clause & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=32463fd29aa303fb2360faeeae17256b"
SECTION = "inputmethods"

SRC_URI = "http://uim.googlecode.com/files/uim-${PV}.tar.bz2"

SRC_URI_append_class-target = " file://uim-module-manager.patch \
    file://0001-fix-bug-for-cross-compile.patch \
"
SRC_URI[md5sum] = "ecea4c597bab1fd4ba98ea84edcece59"
SRC_URI[sha256sum] = "7b1ea803c73f3478917166f04f67cce6e45ad7ea5ab6df99b948c17eb1cb235f"

DEPENDS = "anthy fontconfig libxft libxt glib-2.0 ncurses intltool"
DEPENDS_class-target += "intltool-native gtk+ gtk+3 uim-native takao-fonts"

RDEPENDS_uim = "libuim0 libedit"
RDEPENDS_uim-anthy = "takao-fonts anthy libanthy0"

LEAD_SONAME = "libuim.so.1"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--disable-emacs \
    --without-scim \
    --without-m17nlib \
    --without-prime \
    --without-canna \
    --without-mana \
    --without-eb \
"

CONFIGUREOPTS_remove_class-target = "--disable-silent-rules"

#Because m4 file's find maxdepth=2, so copy the m4 files of the deep depth.
do_configure_prepend () {
    cp ${S}/sigscheme/m4/* ${S}/m4/
}

do_install_append() {
    rm -rf ${D}/${datadir}/applications
}

PACKAGES += "uim-xim uim-utils uim-skk uim-gtk2.0 uim-gtk3 uim-fep uim-common uim-anthy libuim0 libuim-dev"

FILES_${PN} = "${bindir}/uim-help \
    ${libdir}/uim/plugin/libuim-* \
    ${libdir}/libuim-scm* \
    ${libdir}/libgcroots* \
    ${libdir}/uim/plugin/libuim-* \
"

FILES_libuim0 = "${libdir}/uim/plugin/libuim-custom-enabler.* \
    ${libdir}/libuim-custom.so.* \
    ${datadir}/locale/ja/LC_MESSAGES/uim.mo \
    ${datadir}/locale/fr/LC_MESSAGES/uim.mo \
    ${datadir}/locale/ko/LC_MESSAGES/uim.mo \
    ${libdir}/libuim.so.* \
"
FILES_libuim-dev = "${libdir}/libuim*.a \
    ${libdir}/libuim*.la \
    ${libdir}/libuim*.so \
    ${includedir}/uim \
    ${libdir}/pkgconfig/uim.pc \
"
FILES_uim-anthy = "${libdir}/uim/plugin/libuim-anthy.* \
    ${datadir}/uim/anthy*.scm \
"
FILES_${PN}-dbg += "${libdir}/*/*/*/.debug ${libdir}/*/*/.debug"
FILES_${PN}-dev += "${libdir}/uim/plugin/*.la"

FILES_uim-utils = "${bindir}/uim-sh \
    ${bindir}/uim-module-manager \
    ${libexecdir}/uim-helper-server \
"
FILES_uim-xim = "${bindir}/uim-xim \
    ${libexecdir}/uim-candwin-*gtk \
    ${libexecdir}/uim-candwin-*gtk3 \
    ${datadir}/man/man1/uim-xim.1 \
    ${sysconfdir}/X11/xinit/xinput.d/uim* \
"
FILES_uim-common = "${datadir}/uim/pixmaps/*.png \
    ${datadir}/uim \
"
FILES_uim-fep = "${bindir}/uim-fep*"

FILES_uim-gtk2.0 = "${libdir}/gtk-2.0 \
    ${bindir}/uim-toolbar-gtk \
    ${bindir}/uim-toolbar-gtk-systray \
    ${bindir}/uim-*-gtk \
    ${bindir}/uim-input-pad-ja \
    ${libdir}/uim/uim-*-gtk \
"
FILES_uim-gtk3 = "${libdir}/gtk-3.0 \
    ${bindir}/uim-toolbar-gtk3 \
    ${bindir}/uim-toolbar-gtk3-systray \
    ${bindir}/uim-*-gtk3 \
    ${libdir}/uim/uim-*-gtk3 \
"
FILES_uim-skk = "${libdir}/uim/plugin/libuim-skk.* \
    ${datadir}/uim/skk*.scm \
"

pkg_postinst_uim-anthy() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --register anthy --path /etc/uim
    fi
}

pkg_postrm_uim-anthy() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --path /etc/uim --unregister anthy
    fi
}

pkg_prerm_uim-anthy() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --register anthy --path /etc/uim
    fi
}

pkg_postinst_uim-gtk2.0() {
    gtk-query-immodules-2.0 > /etc/gtk-2.0/gtk.immodules
}

pkg_postinst_uim-skk() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --register skk --path /etc/uim
    fi
}

pkg_postrm_uim-skk() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --path /etc/uim --unregister skk
    fi
}

pkg_postinst_uim-common() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --path /etc/uim --register \
            tutcode tcode hangul viqr \
            ipa-x-sampa latin byeoru
    fi
}

pkg_prerm_uim-common() {
    if [ -f /usr/bin/uim-module-manager ]; then
        /usr/bin/uim-module-manager --path /etc/uim --register \
        tutcode tcode hangul viqr ipa-x-sampa latin byeoru
    fi
}

BBCLASSEXTEND = "native"
