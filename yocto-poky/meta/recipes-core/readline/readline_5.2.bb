SUMMARY = "Library for editing typed command lines"
DESCRIPTION = "The GNU Readline library provides a set of functions for use by applications that allow users to edit \
command lines as they are typed in. Both Emacs and vi editing modes are available. The Readline library includes  \
additional functions to maintain a list of previously-entered command lines, to recall and perhaps reedit those   \
lines, and perform csh-like history expansion on previous commands."
SECTION = "libs"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=03b36fdd84f74b8d8189a202b980b67f"

DEPENDS += "ncurses"

PR = "r9"

SRC_URI = "${GNU_MIRROR}/readline/${BPN}-${PV}.tar.gz;name=archive \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-001;name=patch1;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-002;name=patch2;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-003;name=patch3;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-004;name=patch4;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-005;name=patch5;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-006;name=patch6;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-007;name=patch7;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-008;name=patch8;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-009;name=patch9;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-010;name=patch10;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-011;name=patch11;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-012;name=patch12;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-013;name=patch13;apply=yes;striplevel=0 \
           ${GNU_MIRROR}/readline/readline-5.2-patches/readline52-014;name=patch14;apply=yes;striplevel=0 \
           file://configure-fix.patch \
           file://config-dirent-symbols.patch \
           file://fix-redundant-rpath.patch"

SRC_URI[archive.md5sum] = "e39331f32ad14009b9ff49cc10c5e751"
SRC_URI[archive.sha256sum] = "12e88d96aee2cd1192500356f0535540db499282ca7f76339fb4228c31249f45"

SRC_URI[patch1.md5sum]    = "9d4d41622aa9b230c57f68548ce87d8f"
SRC_URI[patch1.sha256sum] = "eac304c369154059f93049ada328739faaf40338d3cb1fb4b544c93d5ce3f8d5"
SRC_URI[patch2.md5sum]    = "f03e512d14206e37f7d6a748b56b9476"
SRC_URI[patch2.sha256sum] = "9deacaef25507a0c2ae0b661bf9342559b59a2954d66ea3c5f5bcd900fdfcf78"
SRC_URI[patch3.md5sum]    = "252b42d8750f1a94b6bdf086612dceb2"
SRC_URI[patch3.sha256sum] = "2a55d2ecb1c9b0147aeb193a6323616ab31c1c525a83b2db3a994b15594ba934"
SRC_URI[patch4.md5sum]    = "a32333c2e603a3ed250514e91050e552"
SRC_URI[patch4.sha256sum] = "a03b65633781efa7c3aae5d57162985e7b7a3c10acf0f2621be610e16f27e5f2"
SRC_URI[patch5.md5sum]    = "8106796c09b789523a3a78ab69c04b6d"
SRC_URI[patch5.sha256sum] = "06001896514148a757ea6edbbd40c4fc4331dc653847244386c37b138b150f64"
SRC_URI[patch6.md5sum]    = "512188e2bf0837f7eca19dbf71f182ae"
SRC_URI[patch6.sha256sum] = "dfef3e982c0adf8bb5a9b7d0468ec8f5f18138b325e28759437464de5be71013"
SRC_URI[patch7.md5sum]    = "ac17aca62eb6fb398c9f2fe9de540aff"
SRC_URI[patch7.sha256sum] = "775b028c7b761397ac6ae1bdfbac7e896dc3b9b3adc2f91312499180ca13bdd1"
SRC_URI[patch8.md5sum]    = "2484c392db021905f112cf97a94dfd4c"
SRC_URI[patch8.sha256sum] = "a21b4e0bf0530b878bad24d5be23d18a9e03a75a31ae30844dc0933bb3d77ecd"
SRC_URI[patch9.md5sum]    = "fc6eb35d07914fae5c57d49c12483ff7"
SRC_URI[patch9.sha256sum] = "138d5e0f0709a47a2d1621295a3dd5e3cc73b63b5cc28dab03abc4e94fe95ecf"
SRC_URI[patch10.md5sum]    = "7a2bf3dc7ac7680b1461a5701100e91b"
SRC_URI[patch10.sha256sum] = "83f8c1aadb86b1a2fad8821a9c6be72a8de5afd7fd9fde58a30b3b57d939693e"
SRC_URI[patch11.md5sum]    = "ef6cef6822663470f6ac8c517c5a7ec6"
SRC_URI[patch11.sha256sum] = "08ad3384ab0906e6fa4cc417eb8c43ff59375bcead15fd5c8e31730f0413b3d6"
SRC_URI[patch12.md5sum]    = "e3e9f441c8111589855bc363e5640f6c"
SRC_URI[patch12.sha256sum] = "20f0243be2299c23213492cc2c19cfd15cc528d2b566a76a2de58306bb9e4c9e"
SRC_URI[patch13.md5sum]    = "3e2e5f543ed268a68fd1fa839faade1a"
SRC_URI[patch13.sha256sum] = "0cc649516a5bdfa61c5e56937407570288b6972d75aa1bd060ad30ebe98144d5"
SRC_URI[patch14.md5sum]    = "a1be30e1c6f1099bb5fcef00a2631fb8"
SRC_URI[patch14.sha256sum] = "6f1a68320d01522ca1ea5a737124ecc8739f3dcbfea2dee21e3ccf839a21a817"

inherit autotools

EXTRA_AUTORECONF += "--exclude=autoheader"

LEAD_SONAME = "libreadline.so"

do_configure_prepend () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		cat ${S}/aclocal.m4 > ${S}/acinclude.m4
	fi
}

do_install_append () {
	# Make install doesn't properly install these
	oe_libinstall -so -C shlib libhistory ${D}${libdir}
	oe_libinstall -so -C shlib libreadline ${D}${libdir}
}

BBCLASSEXTEND = "native nativesdk"
