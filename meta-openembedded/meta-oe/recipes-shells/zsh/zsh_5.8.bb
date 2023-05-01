SUMMARY = "UNIX Shell similar to the Korn shell"
DESCRIPTION = "Zsh is a shell designed for interactive use, although it is also a \
               powerful scripting language. Many of the useful features of bash, \
               ksh, and tcsh were incorporated into zsh; many original features were added."
HOMEPAGE = "http://www.zsh.org"
SECTION = "base/shell"

LICENSE = "zsh"
LIC_FILES_CHKSUM = "file://LICENCE;md5=1a4c4cda3e8096d2fd483ff2f4514fec"

DEPENDS = "ncurses bison-native libcap libpcre gdbm groff-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/5.8/${BP}.tar.xz \
	file://CVE-2021-45444_1.patch \
	file://CVE-2021-45444_2.patch \
	file://CVE-2021-45444_3.patch \
	"
SRC_URI[sha256sum] = "dcc4b54cc5565670a65581760261c163d720991f0d06486da61f8d839b52de27"

inherit autotools-brokensep gettext update-alternatives manpages

EXTRA_OECONF = " \
    --bindir=${base_bindir} \
    --enable-etcdir=${sysconfdir} \
    --enable-fndir=${datadir}/${BPN}/${PV}/functions \
    --enable-site-fndir=${datadir}/${BPN}/site-functions \
    --with-term-lib='ncursesw ncurses' \
    --with-tcsetpgrp \
    --enable-cap \
    --enable-multibyte \
    --disable-gdbm \
    --disable-dynamic \
    zsh_cv_shared_environ=yes \
"

# Configure respects --bindir from EXTRA_OECONF, but then Src/Makefile will read bindir from environment
export bindir="${base_bindir}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

ALTERNATIVE:${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/${BPN}"
ALTERNATIVE_PRIORITY = "90"

export AUTOHEADER = "true"

do_configure () {
    gnu-configize --force ${S}
    oe_runconf
}

pkg_postinst:${PN} () {
    touch $D${sysconfdir}/shells
    grep -q "bin/zsh" $D${sysconfdir}/shells || echo /bin/zsh >> $D${sysconfdir}/shells
    grep -q "bin/sh" $D${sysconfdir}/shells || echo /bin/sh >> $D${sysconfdir}/shells
}

# work around QA failures with usrmerge installing zsh in /usr/bin/zsh instead of /bin/zsh
# ERROR: QA Issue: /usr/share/zsh/5.8/functions/zed contained in package zsh requires /bin/zsh, but no providers found in RDEPENDS:zsh? [file-rdeps]
# like bash does since https://git.openembedded.org/openembedded-core/commit/?id=4759408677a4e60c5fa7131afcb5bc184cf2f90a
RPROVIDES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/zsh', '', d)}"
