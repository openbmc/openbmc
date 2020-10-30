SUMMARY = "Emacs is the extensible, customizable, self-documenting real-time display editor"
HOMEPAGE = "https://www.gnu.org/software/emacs/"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://ftp.gnu.org/pub/gnu/emacs/emacs-${PV}.tar.xz"

SRC_URI_append_class-target = " file://usemake-docfile-native.patch"

SRC_URI[sha256sum] = "4a4c128f915fc937d61edfc273c98106711b540c9be3cd5d2e2b9b5b2f172e41"

PACKAGECONFIG[gnutls] = "--with-gnutls=yes,--with-gnutls=no,gnutls"
PACKAGECONFIG[kerberos] = "--with-kerberos=yes,--with-kerberos=no,krb5"
PACKAGECONFIG[libgmp] = "--with-libgmp=yes,--with-libgmp=no,gmp"

PACKAGECONFIG ??= "gnutls kerberos libgmp"

# We could use --without-all but its better to
# split it into several packages (size of minimal doesnt change)
EXTRA_OECONF = " --with-x=no --with-dumping=none"

DEPENDS = "ncurses"
DEPENDS_append_class-target = " emacs-native"

inherit autotools mime-xdg


do_compile_class-native (){
    cd ${B}/lib-src
    oe_runmake make-docfile
    oe_runmake make-fingerprint
}
do_install_class-native(){
    install -d ${D}${bindir}
    install -m 755 ${B}/lib-src/make-docfile ${D}/${bindir}/
    install -m 755 ${B}/lib-src/make-fingerprint ${D}/${bindir}/
}


do_install_append(){
    # Delete systemd stuff, extend using DISTRO_FEATURES?
    rm -rf ${D}/${libdir}
    # Extra stuff which isnt needed
    rm -rf ${D}/${datadir}/metainfo
    rm -rf ${D}/${datadir}/info
    # Emacs copies files to ${D} while building, which were unpacked
    # by a different user, we need to restore those
    chown -R root:root ${D}${datadir}
}


# Use a similar strategy to how we build python:
# Create three packages
# minimal - A working lisp based text editor
# base - What would probably work for most
# full - A fully working emacs
# The lists of files are long but are worth it
# Installing "emacs" installs the base package
PACKAGE_BEFORE_PN = "${PN}-minimal ${PN}-base ${PN}-full"
RPROVIDES_${PN}-base = "${PN}"
RDEPENDS_${PN}-base_class-target = "${PN}-minimal"
RDEPENDS_${PN}-full_class-target = "${PN}"


# A minimal version of emacs that works
FILES_${PN}-minimal = " \
    ${datadir}/${BPN}/${PV}/lisp/loadup.el \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/byte-run.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/backquote.elc \
    ${datadir}/${BPN}/${PV}/lisp/subr.elc \
    ${datadir}/${BPN}/${PV}/lisp/version.elc \
    ${datadir}/${BPN}/${PV}/lisp/widget.elc \
    ${datadir}/${BPN}/${PV}/lisp/custom.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/map-ynp.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/mule.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/mule-conf.elc \
    ${datadir}/${BPN}/${PV}/lisp/env.elc \
    ${datadir}/${BPN}/${PV}/lisp/format.elc \
    ${datadir}/${BPN}/${PV}/lisp/bindings.elc \
    ${datadir}/${BPN}/${PV}/lisp/window.elc \
    ${datadir}/${BPN}/${PV}/lisp/files.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/macroexp.elc \
    ${datadir}/${BPN}/${PV}/lisp/cus-face.elc \
    ${datadir}/${BPN}/${PV}/lisp/faces.elc \
    ${datadir}/${BPN}/${PV}/lisp/button.elc \
    ${datadir}/${BPN}/${PV}/lisp/loaddefs.el \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/nadvice.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-preloaded.elc \
    ${datadir}/${BPN}/${PV}/lisp/obarray.elc \
    ${datadir}/${BPN}/${PV}/lisp/abbrev.elc \
    ${datadir}/${BPN}/${PV}/lisp/simple.elc \
    ${datadir}/${BPN}/${PV}/lisp/jka-cmpr-hook.elc \
    ${datadir}/${BPN}/${PV}/lisp/epa-hook.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/mule-cmds.elc \
    ${datadir}/${BPN}/${PV}/lisp/case-table.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/charprop.el \
    ${datadir}/${BPN}/${PV}/lisp/international/characters.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/charscript.elc \
    ${datadir}/${BPN}/${PV}/lisp/composite.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/chinese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/cyrillic.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/indian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/sinhala.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/english.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/ethiopic.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/european.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/czech.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/slovak.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/romanian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/greek.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/hebrew.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/cp51932.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/eucjp-ms.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/japanese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/korean.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/lao.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/tai-viet.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/thai.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/tibetan.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/vietnamese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/misc-lang.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/utf-8-lang.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/georgian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/khmer.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/burmese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/cham.elc \
    ${datadir}/${BPN}/${PV}/lisp/indent.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-generic.elc \
    ${datadir}/${BPN}/${PV}/lisp/minibuffer.elc \
    ${datadir}/${BPN}/${PV}/lisp/frame.elc \
    ${datadir}/${BPN}/${PV}/lisp/startup.elc \
    ${datadir}/${BPN}/${PV}/lisp/term/tty-colors.elc \
    ${datadir}/${BPN}/${PV}/lisp/font-core.elc \
    ${datadir}/${BPN}/${PV}/lisp/facemenu.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/syntax.elc \
    ${datadir}/${BPN}/${PV}/lisp/font-lock.elc \
    ${datadir}/${BPN}/${PV}/lisp/jit-lock.elc \
    ${datadir}/${BPN}/${PV}/lisp/mouse.elc \
    ${datadir}/${BPN}/${PV}/lisp/select.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/timer.elc \
    ${datadir}/${BPN}/${PV}/lisp/isearch.elc \
    ${datadir}/${BPN}/${PV}/lisp/rfn-eshadow.elc \
    ${datadir}/${BPN}/${PV}/lisp/menu-bar.elc \
    ${datadir}/${BPN}/${PV}/lisp/tab-bar.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/lisp.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/page.elc \
    ${datadir}/${BPN}/${PV}/lisp/register.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/paragraphs.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/prog-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/lisp-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/elisp-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/text-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/fill.elc \
    ${datadir}/${BPN}/${PV}/lisp/newcomment.elc \
    ${datadir}/${BPN}/${PV}/lisp/replace.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/tabulated-list.elc \
    ${datadir}/${BPN}/${PV}/lisp/buff-menu.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/float-sup.elc \
    ${datadir}/${BPN}/${PV}/lisp/vc/vc-hooks.elc \
    ${datadir}/${BPN}/${PV}/lisp/vc/ediff-hook.elc \
    ${datadir}/${BPN}/${PV}/lisp/uniquify.elc \
    ${datadir}/${BPN}/${PV}/lisp/electric.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/eldoc.elc \
    ${datadir}/${BPN}/${PV}/lisp/cus-start.elc \
    ${datadir}/${BPN}/${PV}/lisp/tooltip.elc \
    ${datadir}/${BPN}/${PV}/lisp/simple.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/regexp-opt.elc \
    ${datadir}/${BPN}/${PV}/lisp/term/xterm.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/bytecomp.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cconv.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/gv.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/byte-opt.elc \
    ${datadir}/${BPN}/${PV}/lisp/image.elc \
    ${datadir}/${BPN}/${PV}/lisp/ldefs-boot.el \
    ${datadir}/${BPN}/${PV}/lisp/help.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/uni*.el \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/vc/warnings.elc \
    ${datadir}/${BPN}/${PV}/etc/charsets/ \
    ${datadir}/${BPN}/${PV}/lisp/disp-table.elc \
    ${bindir}/emacs* \
    ${prefix}/libexec \
"


# What works for "most" is relative, but this can be easily extended if needed
FILES_${PN}-base = " \
    ${datadir}/${BPN}/${PV}/etc/srecode \
    ${datadir}/${BPN}/${PV}/etc/e \
    ${datadir}/${BPN}/${PV}/etc/forms \
    ${datadir}/${BPN}/${PV}/lisp/cedet \
    ${datadir}/${BPN}/${PV}/site-lisp/ \
    ${datadir}/${BPN}/${PV}/lisp/subdirs.el \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-defs.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-vars.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-engine.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-styles.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-fonts.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-cmds.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-align.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-menus.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-guess.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-lib.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-macs.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/pcase.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/inline.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-seq.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/easymenu.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/python* \
    ${datadir}/${BPN}/${PV}/lisp/ansi-color.elc \
    ${datadir}/${BPN}/${PV}/lisp/comint.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/ring.elc \
    ${datadir}/${BPN}/${PV}/lisp/json.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/map.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/seq.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/subr-x.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/seq.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-sh.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-compat.elc \
    ${datadir}/${BPN}/${PV}/lisp/auth-source.elc \
    ${datadir}/${BPN}/${PV}/lisp/password-cache.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/eieio.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/eieio-core.elc \
    ${datadir}/${BPN}/${PV}/lisp/format-spec.elc \
    ${datadir}/${BPN}/${PV}/lisp/ls-lisp.elc \
    ${datadir}/${BPN}/${PV}/lisp/calendar/parse-time.elc \
    ${datadir}/${BPN}/${PV}/lisp/calendar/iso8601.elc \
    ${datadir}/${BPN}/${PV}/lisp/calendar/time-date.elc \
    ${datadir}/${BPN}/${PV}/lisp/shell.elc \
    ${datadir}/${BPN}/${PV}/lisp/pcomplete.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-integration.elc \
    ${datadir}/${BPN}/${PV}/lisp/files-x.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/trampver.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-loaddefs.el \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/*perl* \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/smie.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/*asm* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cpp* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/make* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/sh-script* \
    ${datadir}/${BPN}/${PV}/etc/themes/adwaita-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/wheatgrass-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/deeper-blue-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/light-blue-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/misterioso-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/tango-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/wombat-theme.el \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/prog* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/executable* \
"

# Restore FILES for the full package to catch everything left
FILES_${PN}-full = "${FILES_${PN}}"
FILES_${PN}-full_append = " ${datadir}/icons"


# The following does NOT build a native emacs.
# It only builds some parts of it that are
# required to by the build for target emacs.
BBCLASSEXTEND = "native"
