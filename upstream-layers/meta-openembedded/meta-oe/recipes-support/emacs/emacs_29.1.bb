SUMMARY = "Emacs is the extensible, customizable, self-documenting real-time display editor"
HOMEPAGE = "https://www.gnu.org/software/emacs/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://ftp.gnu.org/pub/gnu/emacs/emacs-${PV}.tar.xz \
          "
SRC_URI:append:class-target = " \
    file://use-emacs-native-tools-for-cross-compiling.patch \
    file://avoid-running-host-binaries-for-sanity.patch \
"

SRC_URI[sha256sum] = "d2f881a5cc231e2f5a03e86f4584b0438f83edd7598a09d24a21bd8d003e2e01"

CVE_STATUS[CVE-2007-6109] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."

PACKAGECONFIG[gnutls] = "--with-gnutls=yes,--with-gnutls=no,gnutls"
PACKAGECONFIG[kerberos] = "--with-kerberos=yes,--with-kerberos=no,krb5"
PACKAGECONFIG[libgmp] = "--with-libgmp=yes,--with-libgmp=no,gmp"
PACKAGECONFIG[selinux] = "--with-selinux=yes,--with-selinux=no,libselinux"

PACKAGECONFIG ??= "gnutls kerberos libgmp"

# We could use --without-all but its better to
# split it into several packages (size of minimal doesnt change)
EXTRA_OECONF = " --with-x=no --with-dumping=none --disable-build-details"

# Disable seccomp, as its a default dependency for gnutls but it doesnt work when cross-compiling emacs
EXTRA_OECONF:append = " ${@bb.utils.contains('PACKAGECONFIG', 'gnutls', 'ac_cv_have_decl_SECCOMP_FILTER_FLAG_TSYNC=no ac_cv_have_decl_SECCOMP_SET_MODE_FILTER=no', '', d)}"


DEPENDS = "ncurses"
DEPENDS:append:class-target = " emacs-native"

inherit autotools mime-xdg pkgconfig


# Create the required native tools for the target build
do_compile:class-native (){
    cd ${B}/lib-src
    oe_runmake make-docfile
    oe_runmake make-fingerprint
    cd ${B}/src
    oe_runmake bootstrap-emacs
}

do_install:class-native(){
    install -d ${D}${bindir}
    install -m 755 ${B}/lib-src/make-docfile ${D}/${bindir}/
    install -m 755 ${B}/lib-src/make-fingerprint ${D}/${bindir}/
    install -m 755 ${B}/src/bootstrap-emacs ${D}/${bindir}/
}

do_compile:prepend:class-target () {
   # export EMACS env variables for the native tools to use to allow calling bootstrap-emacs
    export EMACSLOADPATH=${S}/lisp
    export EMACSDATA=${S}/etc
}


do_install:prepend:class-target(){
    # export EMACS env variables for the native tools to use to allow calling bootstrap-emacs
    export EMACSLOADPATH=${S}/lisp
    export EMACSDATA=${S}/etc
}

# Remove build host references to avoid target pollution
do_compile:prepend () {
    sed -i -e 's|${TMPDIR}||g' ${B}/src/config.h
    sed -i -e 's|${B}||g' ${B}/src/epaths.h
}

do_install:append(){
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
RPROVIDES:${PN}-base = "${PN}"
RDEPENDS:${PN}-base:class-target = "${PN}-minimal"
RDEPENDS:${PN}-full:class-target = "${PN}"


# A minimal version of emacs that works
# These are kept sorted in alphabetical order
FILES:${PN}-minimal = " \
    ${bindir}/emacs* \
    ${datadir}/${BPN}/${PV}/etc/charsets/ \
    ${datadir}/${BPN}/${PV}/lisp/abbrev.elc \
    ${datadir}/${BPN}/${PV}/lisp/bindings.elc \
    ${datadir}/${BPN}/${PV}/lisp/buff-menu.elc \
    ${datadir}/${BPN}/${PV}/lisp/button.elc \
    ${datadir}/${BPN}/${PV}/lisp/case-table.elc \
    ${datadir}/${BPN}/${PV}/lisp/composite.elc \
    ${datadir}/${BPN}/${PV}/lisp/cus-face.elc \
    ${datadir}/${BPN}/${PV}/lisp/cus-start.elc \
    ${datadir}/${BPN}/${PV}/lisp/custom.elc \
    ${datadir}/${BPN}/${PV}/lisp/disp-table.elc \
    ${datadir}/${BPN}/${PV}/lisp/electric.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/backquote.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/byte-opt.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/byte-run.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/bytecomp.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cconv.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-generic.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-lib.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-macs.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-preloaded.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-seq.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/debug-early.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/easy-mmode.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/easymenu.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/eldoc.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/float-sup.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/gv.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/inline.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/lisp-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/lisp.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/macroexp.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/map-ynp.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/map.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/nadvice.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/oclosure.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/pcase.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/regexp-opt.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/rmc.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/rx.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/seq.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/shorthands.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/subr-x.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/syntax.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/tabulated-list.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/timer.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/vc/warnings.elc \
    ${datadir}/${BPN}/${PV}/lisp/env.elc \
    ${datadir}/${BPN}/${PV}/lisp/epa-hook.elc \
    ${datadir}/${BPN}/${PV}/lisp/facemenu.elc \
    ${datadir}/${BPN}/${PV}/lisp/faces.elc \
    ${datadir}/${BPN}/${PV}/lisp/files.elc \
    ${datadir}/${BPN}/${PV}/lisp/font-core.elc \
    ${datadir}/${BPN}/${PV}/lisp/font-lock.elc \
    ${datadir}/${BPN}/${PV}/lisp/format.elc \
    ${datadir}/${BPN}/${PV}/lisp/frame.elc \
    ${datadir}/${BPN}/${PV}/lisp/help.elc \
    ${datadir}/${BPN}/${PV}/lisp/image.elc \
    ${datadir}/${BPN}/${PV}/lisp/indent.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/characters.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/charprop.el \
    ${datadir}/${BPN}/${PV}/lisp/international/charscript.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/cp51932.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/emoji-zwj.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/eucjp-ms.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/iso-transl.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/mule-cmds.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/mule-conf.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/mule.elc \
    ${datadir}/${BPN}/${PV}/lisp/international/uni*.el \
    ${datadir}/${BPN}/${PV}/lisp/isearch.elc \
    ${datadir}/${BPN}/${PV}/lisp/jit-lock.elc \
    ${datadir}/${BPN}/${PV}/lisp/jka-cmpr-hook.elc \
    ${datadir}/${BPN}/${PV}/lisp/jka-compr.elc \
    ${datadir}/${BPN}/${PV}/lisp/keymap.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/burmese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/cham.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/chinese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/cyrillic.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/czech.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/english.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/ethiopic.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/european.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/georgian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/greek.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/hebrew.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/indian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/indonesian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/japanese.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/khmer.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/korean.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/lao.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/misc-lang.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/philippine.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/romanian.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/sinhala.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/slovak.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/tai-viet.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/thai.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/tibetan.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/utf-8-lang.elc \
    ${datadir}/${BPN}/${PV}/lisp/language/vietnamese.elc \
    ${datadir}/${BPN}/${PV}/lisp/ldefs-boot.el \
    ${datadir}/${BPN}/${PV}/lisp/loaddefs.el \
    ${datadir}/${BPN}/${PV}/lisp/loadup.el \
    ${datadir}/${BPN}/${PV}/lisp/menu-bar.elc \
    ${datadir}/${BPN}/${PV}/lisp/minibuffer.elc \
    ${datadir}/${BPN}/${PV}/lisp/mouse.elc \
    ${datadir}/${BPN}/${PV}/lisp/newcomment.elc \
    ${datadir}/${BPN}/${PV}/lisp/obarray.elc \
    ${datadir}/${BPN}/${PV}/lisp/paren.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/elisp-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/prog-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/register.elc \
    ${datadir}/${BPN}/${PV}/lisp/replace.elc \
    ${datadir}/${BPN}/${PV}/lisp/rfn-eshadow.elc \
    ${datadir}/${BPN}/${PV}/lisp/select.elc \
    ${datadir}/${BPN}/${PV}/lisp/simple.elc \
    ${datadir}/${BPN}/${PV}/lisp/startup.elc \
    ${datadir}/${BPN}/${PV}/lisp/subr.elc \
    ${datadir}/${BPN}/${PV}/lisp/tab-bar.elc \
    ${datadir}/${BPN}/${PV}/lisp/term/tty-colors.elc \
    ${datadir}/${BPN}/${PV}/lisp/term/xterm.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/fill.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/page.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/paragraphs.elc \
    ${datadir}/${BPN}/${PV}/lisp/textmodes/text-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/thingatpt.elc \
    ${datadir}/${BPN}/${PV}/lisp/tooltip.elc \
    ${datadir}/${BPN}/${PV}/lisp/uniquify.elc \
    ${datadir}/${BPN}/${PV}/lisp/vc/ediff-hook.elc \
    ${datadir}/${BPN}/${PV}/lisp/vc/vc-hooks.elc \
    ${datadir}/${BPN}/${PV}/lisp/version.elc \
    ${datadir}/${BPN}/${PV}/lisp/widget.elc \
    ${datadir}/${BPN}/${PV}/lisp/window.elc \
    ${prefix}/libexec \
"

# What works for "most" is relative, but this can be easily extended if needed
FILES:${PN}-base = " \
    ${datadir}/${BPN}/${PV}/etc/e \
    ${datadir}/${BPN}/${PV}/etc/forms \
    ${datadir}/${BPN}/${PV}/etc/srecode \
    ${datadir}/${BPN}/${PV}/etc/themes/adwaita-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/deeper-blue-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/light-blue-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/misterioso-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/tango-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/wheatgrass-theme.el \
    ${datadir}/${BPN}/${PV}/etc/themes/wombat-theme.el \
    ${datadir}/${BPN}/${PV}/lisp/ansi-color.elc \
    ${datadir}/${BPN}/${PV}/lisp/auth-source.elc \
    ${datadir}/${BPN}/${PV}/lisp/calendar/iso8601.elc \
    ${datadir}/${BPN}/${PV}/lisp/calendar/parse-time.elc \
    ${datadir}/${BPN}/${PV}/lisp/calendar/time-date.elc \
    ${datadir}/${BPN}/${PV}/lisp/cedet \
    ${datadir}/${BPN}/${PV}/lisp/comint.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-lib.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-macs.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/cl-seq.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/eieio-core.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/eieio.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/inline.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/pcase.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/ring.elc \
    ${datadir}/${BPN}/${PV}/lisp/emacs-lisp/smie.elc \
    ${datadir}/${BPN}/${PV}/lisp/files-x.elc \
    ${datadir}/${BPN}/${PV}/lisp/format-spec.elc \
    ${datadir}/${BPN}/${PV}/lisp/json.elc \
    ${datadir}/${BPN}/${PV}/lisp/ls-lisp.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-compat.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-integration.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-loaddefs.el \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp-sh.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/tramp.elc \
    ${datadir}/${BPN}/${PV}/lisp/net/trampver.elc \
    ${datadir}/${BPN}/${PV}/lisp/password-cache.elc \
    ${datadir}/${BPN}/${PV}/lisp/pcomplete.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/*asm* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/*perl* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-align.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-cmds.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-defs.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-engine.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-fonts.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-guess.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-menus.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-mode.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-styles.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cc-vars.elc \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/cpp* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/executable* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/make* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/prog* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/python* \
    ${datadir}/${BPN}/${PV}/lisp/progmodes/sh-script* \
    ${datadir}/${BPN}/${PV}/lisp/shell.elc \
    ${datadir}/${BPN}/${PV}/lisp/subdirs.el \
    ${datadir}/${BPN}/${PV}/site-lisp/ \
"

# Restore FILES for the full package to catch everything left
FILES:${PN}-full = "${FILES:${PN}}"
FILES:${PN}-full:append = " ${datadir}/icons"


# The following does NOT build a native emacs.
# It only builds some parts of it that are
# required to by the build for target emacs.
BBCLASSEXTEND = "native"
