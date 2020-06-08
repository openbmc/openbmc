DESCRIPTION = "A multilingual user input method library"
HOMEPAGE = "http://uim.freedesktop.org/"
LICENSE = "BSD-3-Clause & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ab2826b41ca0ff4030d38cc39791d1c8"
SECTION = "inputmethods"

SRC_URI = "https://github.com/uim/uim/releases/download/${PV}/uim-${PV}.tar.bz2 \
    file://0001-Fix-to-set-libedit-path-from-configure-option-proper.patch \
    file://JMP_BUF_GC_jmp_buf.patch \
"
SRC_URI_append_class-target = "\
    file://uim-module-manager.patch \
"
SRC_URI[md5sum] = "01c7bd5d0d4f3a9f6f5befe6f57a470b"
SRC_URI[sha256sum] = "34599bbcc4e5ab87832370763e38be5100984a64237555e9234a1ea225a0fadc"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

DEPENDS = "anthy fontconfig libxft libxt glib-2.0 ncurses intltool libedit"
DEPENDS_append_class-target = " intltool-native gtk+ gtk+3 uim-native takao-fonts"

RDEPENDS_uim = "libuim0 libedit"
RDEPENDS_uim-anthy = "takao-fonts anthy libanthy0"
RDEPENDS_uim-anthy_append_libc-glibc = " glibc-utils glibc-gconv-euc-jp"

LEAD_SONAME = "libuim.so.1"

COMPATIBLE_HOST_riscv64 = "null"
COMPATIBLE_HOST_riscv32 = "null"

inherit features_check autotools pkgconfig gettext qemu gtk-immodules-cache

REQUIRED_DISTRO_FEATURES = "x11"

GTKIMMODULES_PACKAGES = "uim-gtk2.0 uim-gtk3"

EXTRA_OECONF += "--disable-emacs \
    --with-libedit=${STAGING_EXECPREFIXDIR} \
    --without-scim \
    --without-m17nlib \
    --without-prime \
    --without-canna \
    --without-mana \
    --without-eb \
"

CONFIGUREOPTS_remove_class-target = "--disable-silent-rules"

# https://github.com/uim/uim/issues/44
PARALLEL_MAKE = ""

#Because m4 file's find maxdepth=2, so copy the m4 files of the deep depth.
do_configure_prepend () {
    cp ${S}/sigscheme/m4/* ${S}/m4/
}

do_install_append() {
    rm -rf ${D}/${datadir}/applications
}

PACKAGES =+ "uim-xim uim-utils uim-skk uim-gtk2.0 uim-gtk3 uim-fep uim-anthy uim-common libuim0 libuim-dev"

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

PACKAGE_WRITE_DEPS += "qemu-native"
pkg_postinst_uim-anthy() {
    if test -n "$D"; then
        ${@qemu_run_binary(d, '$D', '${bindir}/uim-module-manager')} --register anthy --path $D${datadir}/uim
    else
		uim-module-manager --register anthy --path ${datadir}/uim
    fi
}

pkg_prerm_uim-anthy() {
    if test -n "$D"; then
        ${@qemu_run_binary(d, '$D', '${bindir}/uim-module-manager')} --path $D${datadir}/uim --unregister anthy
    else
		uim-module-manager --path ${datadir}/uim --unregister anthy
    fi
}

pkg_postinst_uim-skk() {
    if test -n "$D"; then
        ${@qemu_run_binary(d, '$D', '${bindir}/uim-module-manager')} --register skk --path $D${datadir}/uim
    else
		uim-module-manager --register skk --path ${datadir}/uim
    fi
}

pkg_postrm_uim-skk() {
    if test -n "$D"; then
        ${@qemu_run_binary(d, '$D', '${bindir}/uim-module-manager')} --path $D${datadir}/uim --unregister skk
    else
		uim-module-manager --path ${datadir}/uim --unregister skk
    fi
}

BBCLASSEXTEND = "native"
