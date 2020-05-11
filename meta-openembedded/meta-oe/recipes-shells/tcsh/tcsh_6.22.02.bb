DESCRIPTION = "TENEX C Shell, an enhanced version of Berkeley csh \
    The TENEX C Shell is an enhanced version of the Berkeley Unix C shell. \
    It includes all features of 4.4BSD C shell, plus a command-line editor, \
    programmable word completion, spelling correction and more."

HOMEPAGE = "http://www.tcsh.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://Copyright;md5=575cf2715c3bf894e1f79aec1d4eaaf5"
SECTION = "base"
DEPENDS = "ncurses virtual/crypt gettext-native"
SRC_URI = " \
    https://astron.com/pub/${BPN}/${BP}.tar.gz \
    file://0001-Enable-system-malloc-on-all-linux.patch \
    file://0002-Add-debian-csh-scripts.patch \
"
SRC_URI[md5sum] = "f34909eab33733aecc05d27adc82277b"
SRC_URI[sha256sum] = "ed287158ca1b00ba477e8ea57bac53609838ebcfd05fcb05ca95021b7ebe885b"

EXTRA_OEMAKE += "CC_FOR_GETHOST='${BUILD_CC}'"
inherit autotools

do_compile_prepend() {
    oe_runmake CC_FOR_GETHOST='${BUILD_CC}' CFLAGS='${BUILD_CFLAGS}' gethost
}

do_install_append () {
    oe_runmake install.man DESTDIR=${D}

    install -d ${D}${base_bindir}
    ln -s /usr/bin/tcsh ${D}${base_bindir}/tcsh
    ln -s /usr/bin/tcsh ${D}${base_bindir}/csh
    install -d ${D}${sysconfdir}/csh/login.d
    install -m 0644 ${S}/csh.cshrc ${S}/csh.login ${S}/csh.logout ${S}/complete.tcsh ${D}${sysconfdir}
    install -D -m 0644 ${S}/csh-mode.el ${D}${datadir}/emacs/site-lisp/csh-mode.el
}

FILES_${PN} += "${datadir}/emacs/site-lisp/csh-mode.el"


pkg_postinst_${PN} () {
#!/bin/sh -e
echo /usr/bin/tcsh >> $D/etc/shells
echo /usr/bin/csh >> $D/etc/shells
}
