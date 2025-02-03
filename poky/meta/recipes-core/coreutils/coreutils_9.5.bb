SUMMARY = "The basic file, shell and text manipulation utilities"
DESCRIPTION = "The GNU Core Utilities provide the basic file, shell and text \
manipulation utilities. These are the core utilities which are expected to exist on \
every system."
HOMEPAGE = "http://www.gnu.org/software/coreutils/"
BUGTRACKER = "http://debbugs.gnu.org/coreutils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://src/ls.c;beginline=1;endline=15;md5=9ac94aaed7fd46fd8df7147a9e3410cb \
                    "
DEPENDS = "gmp libcap"
DEPENDS:class-native = ""

inherit autotools gettext texinfo

SRC_URI = "${GNU_MIRROR}/coreutils/${BP}.tar.xz \
           file://remove-usr-local-lib-from-m4.patch \
           file://0001-local.mk-fix-cross-compiling-problem.patch \
           file://intermittent-testfailure.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "cd328edeac92f6a665de9f323c93b712af1858bc2e0d88f3f7100469470a1b8a"

# http://git.savannah.gnu.org/cgit/coreutils.git/commit/?id=v8.27-101-gf5d7c0842
#
CVE_STATUS[CVE-2016-2781] = "disputed: runcon is not really a sandbox command, use `runcon ... setsid ...` to avoid this particular issue."

EXTRA_OECONF:class-target = "--enable-install-program=arch,hostname --libexecdir=${libdir}"
EXTRA_OECONF:class-nativesdk = "--enable-install-program=arch,hostname"

# acl and xattr are not default features
#
PACKAGECONFIG:class-target ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl xattr', d)} \
"

# The lib/oe/path.py requires xattr
PACKAGECONFIG:class-native ??= "xattr"

# oe-core builds need xattr support
PACKAGECONFIG:class-nativesdk ??= "xattr"

# with, without, depends, rdepends
#
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[single-binary] = "--enable-single-binary,--disable-single-binary,,"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"
PACKAGECONFIG[openssl] = "--with-openssl=yes,--with-openssl=no,openssl"

# [ df mktemp nice printenv base64 gets a special treatment and is not included in this
bindir_progs = "arch basename chcon cksum comm csplit cut dir dircolors dirname du \
                env expand expr factor fmt fold groups head hostid id install \
                join link logname md5sum mkfifo nl nohup nproc od paste pathchk \
                pinky pr printf ptx readlink realpath runcon seq sha1sum sha224sum sha256sum \
                sha384sum sha512sum shred shuf sort split sum tac tail tee test timeout \
                tr truncate tsort tty unexpand uniq unlink uptime users vdir wc who whoami yes"

# hostname gets a special treatment and is not included in this
base_bindir_progs = "cat chgrp chmod chown cp date dd echo false hostname kill ln ls mkdir \
                     mknod mv pwd rm rmdir sleep stty sync touch true uname stat"

sbindir_progs = "chroot"

# Split stdbuf into its own package, so one can include
# coreutils-stdbuf without getting the rest of coreutils, but make
# coreutils itself pull in stdbuf, so IMAGE_INSTALL += "coreutils"
# always provides all coreutils
PACKAGE_BEFORE_PN:class-target += "${@bb.utils.contains('PACKAGECONFIG', 'single-binary', '', 'coreutils-stdbuf', d)}"
FILES:coreutils-stdbuf = "${bindir}/stdbuf ${libdir}/coreutils/libstdbuf.so"
RDEPENDS:coreutils:class-target += "${@bb.utils.contains('PACKAGECONFIG', 'single-binary', '', 'coreutils-stdbuf', d)}"

# However, when the single-binary PACKAGECONFIG is used, stdbuf
# functionality is built into the single coreutils binary, so there's
# no point splitting /usr/bin/stdbuf to its own package. Instead, add
# an RPROVIDE so that rdepending on coreutils-stdbuf will work
# regardless of whether single-binary is in effect.
RPROVIDES:coreutils += "${@bb.utils.contains('PACKAGECONFIG', 'single-binary', 'coreutils-stdbuf', '', d)}"

# put getlimits into coreutils-getlimits, because other ptest packages such as
# findutils-ptest may need this command. Note that getlimits is a noinst_PROGRAM
PACKAGE_BEFORE_PN:class-target += "${PN}-getlimits"
FILES:${PN}-getlimits = "${bindir}/getlimits"

# Let aclocal use the relative path for the m4 file rather than the
# absolute since coreutils has a lot of m4 files, otherwise there might
# be an "Argument list too long" error when it is built in a long/deep
# directory.
acpaths = "-I ./m4"

# Deal with a separate builddir failure if src doesn't exist when creating version.c/version.h
do_compile:prepend () {
	mkdir -p ${B}/src
}

do_install:class-native() {
	autotools_do_install
	# remove groups to fix conflict with shadow-native
	rm -f ${D}${STAGING_BINDIR_NATIVE}/groups
	# The return is a must since native doesn't need the
	# do_install:append() in the below.
	return
}

do_install:append() {
	for i in df mktemp nice printenv base64; do mv ${D}${bindir}/$i ${D}${bindir}/$i.${BPN}; done

	install -d ${D}${base_bindir}
	[ "${base_bindir}" != "${bindir}" ] && for i in ${base_bindir_progs}; do mv ${D}${bindir}/$i ${D}${base_bindir}/$i.${BPN}; done

	install -d ${D}${sbindir}
	[ "${sbindir}" != "${bindir}" ] && for i in ${sbindir_progs}; do mv ${D}${bindir}/$i ${D}${sbindir}/$i.${BPN}; done

	# [ requires special handling because [.coreutils will cause the sed stuff
	# in update-alternatives to fail, therefore use lbracket - the name used
	# for the actual source file.
	mv ${D}${bindir}/[ ${D}${bindir}/lbracket.${BPN}

	# this getlimits noinst_PROGRAM would possibly be needed by other ptest packages
	install ${B}/src/getlimits ${D}/${bindir}
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
# Make hostname's priority higher than busybox but lower than net-tools
ALTERNATIVE_PRIORITY[hostname] = "90"
ALTERNATIVE:${PN} = "lbracket ${bindir_progs} ${base_bindir_progs} ${sbindir_progs} base32 base64 nice printenv mktemp df"
ALTERNATIVE:${PN}-doc = "base64.1 nice.1 mktemp.1 df.1 groups.1 kill.1 uptime.1 stat.1 hostname.1"

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

ALTERNATIVE_LINK_NAME[nice] = "${base_bindir}/nice"
ALTERNATIVE_TARGET[nice] = "${bindir}/nice.${BPN}"
ALTERNATIVE_LINK_NAME[nice.1] = "${mandir}/man1/nice.1"

ALTERNATIVE_LINK_NAME[printenv] = "${base_bindir}/printenv"
ALTERNATIVE_TARGET[printenv] = "${bindir}/printenv.${BPN}"

ALTERNATIVE_LINK_NAME[lbracket] = "${bindir}/["
ALTERNATIVE_TARGET[lbracket] = "${bindir}/lbracket.${BPN}"

ALTERNATIVE_LINK_NAME[groups.1] = "${mandir}/man1/groups.1"
ALTERNATIVE_LINK_NAME[uptime.1] = "${mandir}/man1/uptime.1"
ALTERNATIVE_LINK_NAME[kill.1] = "${mandir}/man1/kill.1"
ALTERNATIVE_LINK_NAME[stat.1] = "${mandir}/man1/stat.1"

# The statx() requires glibc >= 2.28 and linux kernel >= 4.11, it doesn't work
# when glibc >= 2.28 ((Ubuntu 20.04 in docker) and kernel < 4.11 (Host OS
# CentOS 7), we can check kernel version and disable it, but that would cause
# two different signatures for coreutils-native, so disable it unconditionally
# for deterministic build.
EXTRA_OECONF:append:class-native = " ac_cv_func_statx=0"

python __anonymous() {
    for prog in d.getVar('base_bindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_bindir'), prog))

    for prog in d.getVar('sbindir_progs').split():
        d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('sbindir'), prog))
}

BBCLASSEXTEND = "native nativesdk"

inherit ptest

RDEPENDS:${PN}-ptest += "bash findutils gawk make perl perl-modules python3-core sed shadow"

# -dev automatic dependencies fails as we don't want libmodule-build-perl-dev, its too heavy
# may need tweaking if DEPENDS changes
# Can't use ${PN}-dev here since flags with overrides and key expansion not supported
RRECOMMENDS:coreutils-dev[nodeprrecs] = "1"
DEVDEPS = "acl-dev attr-dev gmp-dev libcap-dev bash-dev findutils-dev gawk-dev shadow-dev"
DEVDEPS:class-native = ""
RRECOMMENDS:${PN}-dev += "${DEVDEPS}"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -r ${S}/tests/* ${D}${PTEST_PATH}/tests
    sed -i 's/ginstall/install/g'  `grep -R ginstall ${D}${PTEST_PATH}/tests | awk -F: '{print $1}' | uniq`
    install -d ${D}${PTEST_PATH}/build-aux
    install ${S}/build-aux/test-driver ${D}${PTEST_PATH}/build-aux/
    install -Dm 0644 ${B}/lib/config.h ${D}${PTEST_PATH}/lib/config.h
    cp ${B}/Makefile ${D}${PTEST_PATH}/
    cp ${S}/init.cfg ${D}${PTEST_PATH}/
    cp -r ${B}/src ${D}${PTEST_PATH}/
    cp -r ${S}/src/*.c ${D}${PTEST_PATH}/src
    sed -i '/^VPATH/s/= .*$/= ./g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^PROGRAMS/s/^/#/g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^Makefile: /s/^.*$/Makefile:/g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^abs_srcdir/s/= .*$/= \$\{PWD\}/g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^abs_top_builddir/s/= .*$/= \$\{PWD\}/g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^abs_top_srcdir/s/= .*$/= \$\{PWD\}/g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^built_programs/s/ginstall/install/g' ${D}${PTEST_PATH}/Makefile
    sed -i '/^CC =/s/ --sysroot=.*recipe-sysroot/ /g' ${D}${PTEST_PATH}/Makefile
    chmod -R 777 ${D}${PTEST_PATH}

    # Disable subcase stty-pairs.sh, it will cause test framework hang
    sed -i '/stty-pairs.sh/d' ${D}${PTEST_PATH}/Makefile

    # Disable subcase tail-2/assert.sh as it has issues on 32-bit systems
    sed -i '/assert.sh/d' ${D}${PTEST_PATH}/Makefile

    # Tweak test d_type-check to use python3 instead of python
    sed -i "1s@.*@#!/usr/bin/python3@" ${D}${PTEST_PATH}/tests/d_type-check
    
    # handle multilib
    sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
}

do_install_ptest:append:libc-musl () {
    # these tests fail due to bash on musl systems
    # xmalloc: cannot allocate 16146 bytes
    sed -i -e '/tests\/dd\/no-allocate.sh/d' ${D}${PTEST_PATH}/Makefile
    sed -i -e '/tests\/split\/line-bytes.sh/d' ${D}${PTEST_PATH}/Makefile
}
RDEPENDS:${PN}-ptest += "${PN}-getlimits"
