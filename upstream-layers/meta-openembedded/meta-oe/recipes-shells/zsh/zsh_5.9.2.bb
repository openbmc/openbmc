SUMMARY = "UNIX Shell similar to the Korn shell"
DESCRIPTION = "Zsh is a shell designed for interactive use, although it is also a \
               powerful scripting language. Many of the useful features of bash, \
               ksh, and tcsh were incorporated into zsh; many original features were added."
HOMEPAGE = "http://www.zsh.org"
SECTION = "base/shell"

LICENSE = "LicenseRef-zsh"
LIC_FILES_CHKSUM = "file://LICENCE;md5=1a4c4cda3e8096d2fd483ff2f4514fec"

DEPENDS = "ncurses bison-native libcap groff-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${PV}/${BP}.tar.xz \
           file://pcre2-config \
"
SRC_URI[sha256sum] = "36fa734374b44783582cec09bcd67822e2f992c779ec1624ab5596df078d2f81"

# The SourceForge download mirror has no browsable listing; check the per-release
# directories in the project's file area instead.
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/zsh/files/zsh/"
UPSTREAM_CHECK_REGEX = "zsh/(?P<pver>\d+(\.\d+)+)/"

inherit autotools gettext update-alternatives manpages

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = " \
    --bindir=${base_bindir} \
    --enable-etcdir=${sysconfdir} \
    --enable-fndir=${datadir}/${BPN}/${PV}/functions \
    --enable-site-fndir=${datadir}/${BPN}/site-functions \
    --with-term-lib='ncursesw ncurses' \
    --enable-cap \
    --enable-multibyte \
    zsh_cv_shared_environ=yes \
"

# Configure respects --bindir from EXTRA_OECONF, but then Src/Makefile will read bindir from environment
export bindir = "${base_bindir}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

ALTERNATIVE:${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/${BPN}"
ALTERNATIVE_PRIORITY = "90"

PACKAGECONFIG ??= "static"
PACKAGECONFIG[static] = "--disable-dynamic,--enable-dynamic zsh_cv_sys_dynamic_rtld_global=yes zsh_cv_sys_dynamic_execsyms=yes"
PACKAGECONFIG[gdbm] = "--enable-gdbm,--disable-gdbm,gdbm,,,static"
PACKAGECONFIG[pcre] = "--enable-pcre PCRE_CONFIG=${UNPACKDIR}/pcre2-config,--disable-pcre,libpcre2 pkgconfig-native,,,static"
# Needed for manpages.bbclass, but they're always installed
PACKAGECONFIG[manpages] = ""

do_install:append() {
    sed -i -e '1!b; s:^#!.*[ /]zsh:#!${bindir}/zsh:; s#/usr/local/bin#${bindir}#;' \
        `find ${D}/usr/share/zsh/${PV}/functions -type f`
}

pkg_postinst:${PN} () {
    touch $D${sysconfdir}/shells
    for i in zsh sh
    do
        grep -q "bin/$i" $D${sysconfdir}/shells || \
            printf >> $D${sysconfdir}/shells \
            "${bindir}/$i\n${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/$i\n', '', d)}"
    done
}
