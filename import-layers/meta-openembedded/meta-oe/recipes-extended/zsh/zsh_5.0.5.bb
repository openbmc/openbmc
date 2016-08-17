SUMMARY = "UNIX Shell similar to the Korn shell"
DESCRIPTION = "Zsh is a shell designed for interactive use, although it is also a \
               powerful scripting language. Many of the useful features of bash, \
               ksh, and tcsh were incorporated into zsh; many original features were added."
HOMEPAGE = "http://www.zsh.org"
SECTION = "base/shell"

LICENSE = "zsh"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b7bc853894664be455a922db9805288e"

DEPENDS = "ncurses bison-native libcap libpcre gdbm groff-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "6fb0e3e52a0f8de5ca63138391b81ce0"
SRC_URI[sha256sum] = "b35cf19e4a6ba39fd03c6372b8a8760a491cc2e2e4aba3d15023ff291c2894b4"

inherit autotools gettext update-alternatives

EXTRA_OECONF = " \
    --bindir=${base_bindir} \
    --enable-etcdir=${sysconfdir} \
    --enable-fndir=${datadir}/${PN}/${PV}/functions \
    --enable-site-fndir=${datadir}/${PN}/site-functions \
    --with-term-lib='ncursesw ncurses' \
    --with-tcsetpgrp \
    --enable-cap \
    --enable-multibyte \
    --disable-gdbm \
    --disable-dynamic \
    zsh_cv_shared_environ=yes \
"

EXTRA_OEMAKE = "-e MAKEFLAGS="

ALTERNATIVE_${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/${BPN}"
ALTERNATIVE_PRIORITY = "100"

export AUTOHEADER = "true"

do_configure () {
    gnu-configize --force ${S}
    oe_runconf
}

do_install_append () {
    rm -fr ${D}/usr/share
}

pkg_postinst_${PN} () {
    touch $D${sysconfdir}/shells
    grep -q "bin/zsh" $D${sysconfdir}/shells || echo /bin/zsh >> $D${sysconfdir}/shells
    grep -q "bin/sh" $D${sysconfdir}/shells || echo /bin/sh >> $D${sysconfdir}/shells
}

FILES_${PN}-dbg += "\
    ${libdir}/${PN}/${PV}/${PN}/.debug/*.so \
    ${libdir}/${PN}/${PV}/${PN}/db/.debug/*.so \
    ${libdir}/${PN}/${PV}/${PN}/net/.debug/*.so \
"
