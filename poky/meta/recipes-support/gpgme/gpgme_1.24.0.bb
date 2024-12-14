SUMMARY = "High-level GnuPG encryption/signing API"
DESCRIPTION = "GnuPG Made Easy (GPGME) is a library designed to make access to GnuPG easier for applications. It provides a High-Level Crypto API for encryption, decryption, signing, signature verification and key management"
HOMEPAGE = "http://www.gnupg.org/gpgme.html"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & GPL-3.0-or-later"
LICENSE:${PN} = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN}-cpp = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN}-tool = "GPL-3.0-or-later"
LICENSE:python3-gpg = "GPL-2.0-or-later & LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LESSER;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://src/gpgme.h.in;endline=23;md5=2f0bf06d1c7dcb28532a9d0f94a7ca1d \
                    file://src/engine.h;endline=22;md5=4b6d8ba313d9b564cc4d4cfb1640af9d \
                    file://src/gpgme-tool.c;endline=21;md5=66c5381e0e05475792e24982d15e7ce8 \
                    "

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/gpgme/${BP}.tar.bz2 \
           file://0001-Revert-build-Make-gpgme.m4-use-gpgrt-config-with-.pc.patch \
           file://0001-pkgconfig.patch \
           file://0002-gpgme-lang-python-gpg-error-config-should-not-be-use.patch \
           file://0003-Correctly-install-python-modules.patch \
           file://0005-gpgme-config-skip-all-lib-or-usr-lib-directories-in-.patch \
           file://0006-fix-build-path-issue.patch \
           file://0001-use-closefrom-on-linux-and-glibc-2.34.patch \
           file://0001-posix-io.c-Use-off_t-instead-of-off64_t.patch \
           file://0001-autogen.sh-remove-unknown-in-version.patch \
           "

SRC_URI[sha256sum] = "61e3a6ad89323fecfaff176bc1728fb8c3312f2faa83424d9d5077ba20f5f7da"

PYTHON_DEPS = "${@bb.utils.contains('LANGUAGES', 'python', 'swig-native', '', d)}"

DEPENDS = "libgpg-error libassuan ${PYTHON_DEPS}"
RDEPENDS:${PN}-cpp += "libstdc++"

RDEPENDS:python3-gpg += "python3-unixadmin"

RRECOMMENDS:${PN} += "${PN}-tool"

BINCONFIG = "${bindir}/gpgme-config"

# Default in configure.ac: "cl cpp python qt"
# Supported: "cl cpp python python2 python3 qt"
# python says 'search and find python2 or python3'

# Building the C++ bindings for native requires a C++ compiler with C++11
# support. Since these bindings are currently not needed, we can disable them.
DEFAULT_LANGUAGES = ""
DEFAULT_LANGUAGES:class-target = "cpp"
LANGUAGES ?= "${DEFAULT_LANGUAGES}"

PYTHON_INHERIT = "${@bb.utils.contains('LANGUAGES', 'python', 'setuptools3-base', '', d)}"

EXTRA_OECONF += '--enable-languages="${LANGUAGES}" \
                 --disable-gpgconf-test \
                 --disable-gpg-test \
                 --disable-gpgsm-test \
                 --disable-g13-test \
'

inherit autotools texinfo binconfig-disabled pkgconfig multilib_header
inherit_defer ${PYTHON_INHERIT} python3native

export PKG_CONFIG='pkg-config'

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "${PN}-cpp ${PN}-tool python3-gpg"

FILES:${PN}-cpp = "${libdir}/libgpgmepp.so.*"
FILES:${PN}-tool = "${bindir}/gpgme-tool"
FILES:python3-gpg = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES:${PN}-dev += "${datadir}/common-lisp/source/gpgme/*"

CFLAGS:append:libc-musl = " -D__error_t_defined "
CACHED_CONFIGUREVARS:libc-musl = "ac_cv_sys_file_offset_bits=no"

do_configure:prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
	rm -f ${S}/m4/libassuan.m4
	rm -f ${S}/m4/python.m4
}

do_install:append() {
       oe_multilib_header gpgme.h
}
