SUMMARY = "Set of GPL'ed utilities to ext2/ext3 filesystem."
DESCRIPTION = "e2tools is a simple set of GPL'ed utilities to read, write, \
and manipulate files in an ext2/ext3 filesystem. These utilities access a \
filesystem directly using the ext2fs library. Can also be used on a Linux \
machine to read/write to disk images or floppies without having to mount \
them or have root access."
HOMEPAGE = "https://github.com/e2tools/e2tools"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "coreutils e2fsprogs"

PV = "0.1.0+git"

SRC_URI = " \
           git://github.com/e2tools/e2tools;protocol=https;branch=master \
           file://run-ptest \
"

SRCREV = "fd092754a6b65c3a769f74f888668c066f09c36d"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest

do_configure:prepend() {
    git -C "${WORKDIR}/git" reset --hard HEAD

    # To install ptest for this package, special configuration needs to be
    # done before do_configure(). So, do_configure_ptest() which is scheduled
    # after do_configure() cannot be used.

    # We only do special configuration if we are installing ptest for this
    # package.
    if [ "${@d.getVar('PTEST_ENABLED')}" -eq "1" ]; then
        # Since we guarantee run-time dependency when installing the ptest for
        # this package, we do not need the check macros under section "checks
        # for programs" in "configure.ac". Plus, these check macros set the
        # ouput variables to incorrect values as these checks are performed on
        # the host environment. Still, we need these variables outputted from
        # these check macros. So, we insert the following lines to manually
        # set these output variables to the correct value in "configure.ac".

        # Note that HAVE_DD_COMMAND and HAVE_MKE2FS_COMMAND are only ever used
        # in tests/Makefile-files which determines whether to include the test
        # cases. As for output variables CHMOD, DD, and MKE2FS, they only
        # point to the programs which test cases need to run. Since these
        # commands are guaranteed to be present due to RDEPENDS and are
        # guaranteed to be accessible under PATH environment variable on the
        # target, we only need to specify the name of these programs.

        perl -i -0777 -pe 's/(^dnl\s*=+\s*^dnl\s*Checks for compiler flags\s*^dnl\s*=+)/
AC_SUBST([CHMOD], 'chmod')
AC_SUBST([DD], 'dd')
AC_SUBST([MKE2FS], 'mke2fs')
AM_CONDITIONAL([HAVE_DD_COMMAND], [true])
AM_CONDITIONAL([HAVE_MKE2FS_COMMAND], [true])
\1/ms' "${WORKDIR}/git/configure.ac"
    fi
}

do_install_ptest() {
    rm -rf "${D}${PTEST_PATH}/*"
    cp -r ../build "${D}${PTEST_PATH}"
    cp -r "${S}/build-aux" "${D}${PTEST_PATH}/build"
    cp -r "${S}" "${D}${PTEST_PATH}"
}

RDEPENDS:${PN}-ptest += "bash coreutils e2fsprogs e2tools gawk make perl"

BBCLASSEXTEND = "native"
