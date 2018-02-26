SUMMARY = "The basic file, shell and text manipulation utilities"
DESCRIPTION = "The GNU Core Utilities provide the basic file, shell and text \
manipulation utilities. These are the core utilities which are expected to exist on \
every system."
HOMEPAGE = "http://www.gnu.org/software/coreutils/"
BUGTRACKER = "http://debbugs.gnu.org/coreutils"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
                    file://src/ls.c;beginline=5;endline=16;md5=38b79785ca88537b75871782a2a3c6b8"
DEPENDS = "gmp libcap"
DEPENDS_class-native = ""

inherit autotools gettext texinfo

SRC_URI = "${GNU_MIRROR}/coreutils/${BP}.tar.xz;name=tarball \
           http://distfiles.gentoo.org/distfiles/${BP}-man.tar.xz;name=manpages \
           file://man-decouple-manpages-from-build.patch \
           file://remove-usr-local-lib-from-m4.patch \
           file://fix-selinux-flask.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0001-uname-report-processor-and-hardware-correctly.patch \
           file://disable-ls-output-quoting.patch \
           file://0001-local.mk-fix-cross-compiling-problem.patch \
          "

SRC_URI[tarball.md5sum] = "502795792c212932365e077946d353ae"
SRC_URI[tarball.sha256sum] = "8891d349ee87b9ff7870f52b6d9312a9db672d2439d289bc57084771ca21656b"
SRC_URI[manpages.md5sum] = "1b31a688d06764e0e94aa20b7ea08222"
SRC_URI[manpages.sha256sum] = "1f615819e9167646c731636b6c5ecbe79837e82a18666bacc82c3fb1dfcfaea3"

EXTRA_OECONF_class-native = "--without-gmp"
EXTRA_OECONF_class-target = "--enable-install-program=arch --libexecdir=${libdir}"
EXTRA_OECONF_class-nativesdk = "--enable-install-program=arch"

# acl and xattr are not default features
#
PACKAGECONFIG_class-target ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl xattr', d)} \
"

# The lib/oe/path.py requires xattr
PACKAGECONFIG_class-native ??= "xattr"

# with, without, depends, rdepends
#
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"

# [ df mktemp base64 gets a special treatment and is not included in this
bindir_progs = "arch basename chcon cksum comm csplit cut dir dircolors dirname du \
                env expand expr factor fmt fold groups head hostid id install \
                join link logname md5sum mkfifo nice nl nohup nproc od paste pathchk \
                pinky pr printenv printf ptx readlink realpath runcon seq sha1sum sha224sum sha256sum \
                sha384sum sha512sum shred shuf sort split stdbuf sum tac tail tee test timeout\
                tr truncate tsort tty unexpand uniq unlink uptime users vdir wc who whoami yes"

# hostname gets a special treatment and is not included in this
base_bindir_progs = "cat chgrp chmod chown cp date dd echo false kill ln ls mkdir \
                     mknod mv pwd rm rmdir sleep stty sync touch true uname stat"

sbindir_progs= "chroot"

# Let aclocal use the relative path for the m4 file rather than the
# absolute since coreutils has a lot of m4 files, otherwise there might
# be an "Argument list too long" error when it is built in a long/deep
# directory.
acpaths = "-I ./m4"

# Deal with a separate builddir failure if src doesn't exist when creating version.c/version.h
do_compile_prepend () {
	mkdir -p ${B}/src
}

do_install_class-native() {
	autotools_do_install
	# remove groups to fix conflict with shadow-native
	rm -f ${D}${STAGING_BINDIR_NATIVE}/groups
	# The return is a must since native doesn't need the
	# do_install_append() in the below.
	return
}

do_install_append() {
	for i in df mktemp base64; do mv ${D}${bindir}/$i ${D}${bindir}/$i.${BPN}; done

	install -d ${D}${base_bindir}
	[ "${base_bindir}" != "${bindir}" ] && for i in ${base_bindir_progs}; do mv ${D}${bindir}/$i ${D}${base_bindir}/$i.${BPN}; done

	install -d ${D}${sbindir}
	[ "${sbindir}" != "${bindir}" ] && for i in ${sbindir_progs}; do mv ${D}${bindir}/$i ${D}${sbindir}/$i.${BPN}; done

	# [ requires special handling because [.coreutils will cause the sed stuff
	# in update-alternatives to fail, therefore use lbracket - the name used
	# for the actual source file.
	mv ${D}${bindir}/[ ${D}${bindir}/lbracket.${BPN}

	# prebuilt man pages
	install -d ${D}/${mandir}/man1
	install -t ${D}/${mandir}/man1 ${S}/man/*.1
	# prebuilt man pages don't do a separate man page for [ vs test.
	# see comment above r.e. sed and update-alternatives
	cp -R --no-dereference --preserve=mode,links -v ${D}${mandir}/man1/test.1 ${D}${mandir}/man1/lbracket.1.${BPN}
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "lbracket ${bindir_progs} ${base_bindir_progs} ${sbindir_progs} base64 mktemp df"
ALTERNATIVE_${PN}-doc = "base64.1 mktemp.1 df.1 lbracket.1 groups.1 kill.1 uptime.1 stat.1  hostname.1"

ALTERNATIVE_LINK_NAME[hostname.1] = "${mandir}/man1/hostname.1"

ALTERNATIVE_LINK_NAME[base64] = "${base_bindir}/base64"
ALTERNATIVE_TARGET[base64] = "${bindir}/base64.${BPN}"
ALTERNATIVE_LINK_NAME[base64.1] = "${mandir}/man1/base64.1"

ALTERNATIVE_LINK_NAME[mktemp] = "${base_bindir}/mktemp"
ALTERNATIVE_TARGET[mktemp] = "${bindir}/mktemp.${BPN}"
ALTERNATIVE_LINK_NAME[mktemp.1] = "${mandir}/man1/mktemp.1"

ALTERNATIVE_LINK_NAME[df] = "${base_bindir}/df"
ALTERNATIVE_TARGET[df] = "${bindir}/df.${BPN}"
ALTERNATIVE_LINK_NAME[df.1] = "${mandir}/man1/df.1"

ALTERNATIVE_LINK_NAME[lbracket] = "${bindir}/["
ALTERNATIVE_TARGET[lbracket] = "${bindir}/lbracket.${BPN}"
ALTERNATIVE_LINK_NAME[lbracket.1] = "${mandir}/man1/lbracket.1"

ALTERNATIVE_LINK_NAME[groups.1] = "${mandir}/man1/groups.1"
ALTERNATIVE_LINK_NAME[uptime.1] = "${mandir}/man1/uptime.1"
ALTERNATIVE_LINK_NAME[kill.1] = "${mandir}/man1/kill.1"
ALTERNATIVE_LINK_NAME[stat.1] = "${mandir}/man1/stat.1"

python __anonymous() {
	for prog in d.getVar('base_bindir_progs').split():
		d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir'), prog))

	for prog in d.getVar('sbindir_progs').split():
		d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('sbindir'), prog))
}

BBCLASSEXTEND = "native nativesdk"
