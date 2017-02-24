DESCRIPTION = "TENEX C Shell, an enhanced version of Berkeley csh \
    The TENEX C Shell is an enhanced version of the Berkeley Unix C shell. \
    It includes all features of 4.4BSD C shell, plus a command-line editor, \
    programmable word completion, spelling correction and more."

HOMEPAGE = "http://www.tcsh.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://Copyright;md5=1cf29be62df2be1a3763118b25b4c780"
SECTION = "base"
DEPENDS = "ncurses gettext-native"
SRC_URI = " \
    ${DEBIAN_MIRROR}/main/t/tcsh/tcsh_${PV}.orig.tar.gz;name=tarball \
    ${DEBIAN_MIRROR}/main/t/tcsh/tcsh_${PV}-2.diff.gz;name=diffs2 \
    ${DEBIAN_MIRROR}/main/t/tcsh/tcsh_${PV}-5.diff.gz;name=diffs5 \
    file://01_build.1.patch \
    file://01_build.2.patch \
    file://01_build.3.patch \
    file://15_no-strip.patch \
    file://disable-test-notty.patch \
    file://disable-test-nice.patch \
    file://disable-lexical.at-31.patch \
    file://12_unknown_lscolors.patch \
    file://tcsh-6.17.02-multibyte.patch \
    file://disable-broken-test.patch \
    file://fix-gcc6-wait-union.patch \
"
SRC_URI[tarball.md5sum] = "6eed09dbd4223ab5b6955378450d228a"
SRC_URI[tarball.sha256sum] = "d81ca27851f3e8545666399b4bcf25433e602a195113b3f7c73886fef84c9fa8"
SRC_URI[diffs2.md5sum] = "ea39b818b624aca49ebf2cd2708d6ff9"
SRC_URI[diffs2.sha256sum] = "95b0c1a339b745c47c5d2f9d02c22a71597462e2e882b51614a9d1f75bd3d16c"
SRC_URI[diffs5.md5sum] = "d536c12a02dc48c332cc472b86927319"
SRC_URI[diffs5.sha256sum] = "7548d64bf996548bfbc13f3e0959fd2e8455f8375381a31da67d79554aabc7af"

inherit autotools

do_install_append () {
    oe_runmake install.man DESTDIR=${D}

    install -d ${D}${base_bindir}
    ln -s /usr/bin/tcsh ${D}${base_bindir}/tcsh

    install -d ${D}${sysconfdir}/csh/login.d
    install -m 0644 ${S}/debian/csh.cshrc ${S}/debian/csh.login ${S}/debian/csh.logout ${S}/complete.tcsh ${D}${sysconfdir}
    install -D -m 0644 ${S}/csh-mode.el ${D}${datadir}/emacs/site-lisp/csh-mode.el
}

FILES_${PN} += "${datadir}/emacs/site-lisp/csh-mode.el"


pkg_postinst_${PN} () {
#!/bin/sh -e
echo /usr/bin/tcsh >> $D/etc/shells
}
