SUMMARY = "High-level GnuPG encryption/signing API"
DESCRIPTION = "GnuPG Made Easy (GPGME) is a library designed to make access to GnuPG easier for applications. It provides a High-Level Crypto API for encryption, decryption, signing, signature verification and key management"
HOMEPAGE = "http://www.gnupg.org/gpgme.html"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LESSER;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://src/gpgme.h.in;endline=23;md5=2f0bf06d1c7dcb28532a9d0f94a7ca1d \
                    file://src/engine.h;endline=22;md5=4b6d8ba313d9b564cc4d4cfb1640af9d"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/gpgme/${BP}.tar.bz2 \
           file://0001-Revert-build-Make-gpgme.m4-use-gpgrt-config-with-.pc.patch \
           file://0001-pkgconfig.patch \
           file://0002-gpgme-lang-python-gpg-error-config-should-not-be-use.patch \
           file://0003-Correctly-install-python-modules.patch \
           file://0004-python-import.patch \
           file://0005-gpgme-config-skip-all-lib-or-usr-lib-directories-in-.patch \
           file://0006-fix-build-path-issue.patch \
           file://0007-python-Add-variables-to-tests.patch \
           file://0008-do-not-auto-check-var-PYTHON.patch \
          "

SRC_URI[sha256sum] = "eebc3c1b27f1c8979896ff361ba9bb4778b508b2496c2fc10e3775a40b1de1ad"

DEPENDS = "libgpg-error libassuan"
RDEPENDS_${PN}-cpp += "libstdc++"

RDEPENDS_python2-gpg += "python-unixadmin"
RDEPENDS_python3-gpg += "python3-unixadmin"

BINCONFIG = "${bindir}/gpgme-config"

# Note select python2 or python3, but you can't select both at the same time
PACKAGECONFIG ??= "python3"
PACKAGECONFIG[python2] = ",,python swig-native,"
PACKAGECONFIG[python3] = ",,python3 swig-native,"

# Default in configure.ac: "cl cpp python qt"
# Supported: "cl cpp python python2 python3 qt"
# python says 'search and find python2 or python3'

# Building the C++ bindings for native requires a C++ compiler with C++11
# support. Since these bindings are currently not needed, we can disable them.
DEFAULT_LANGUAGES = ""
DEFAULT_LANGUAGES_class-target = "cpp"
LANGUAGES ?= "${DEFAULT_LANGUAGES} python"

PYTHON_INHERIT = "${@bb.utils.contains('PACKAGECONFIG', 'python2', 'pythonnative', '', d)}"
PYTHON_INHERIT .= "${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native python3targetconfig', '', d)}"

EXTRA_OECONF += '--enable-languages="${LANGUAGES}" \
                 --disable-gpgconf-test \
                 --disable-gpg-test \
                 --disable-gpgsm-test \
                 --disable-g13-test \
                 --disable-lang-python-test \
'

inherit autotools texinfo binconfig-disabled pkgconfig distutils-common-base ${PYTHON_INHERIT} multilib_header

export PKG_CONFIG='pkg-config'

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "${PN}-cpp"
PACKAGES =. "${@bb.utils.contains('PACKAGECONFIG', 'python2', 'python2-gpg ', '', d)}"
PACKAGES =. "${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3-gpg ', '', d)}"

FILES_${PN}-cpp = "${libdir}/libgpgmepp.so.*"
FILES_python2-gpg = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES_python3-gpg = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-dev += "${datadir}/common-lisp/source/gpgme/* \
                    ${libdir}/cmake/* \
"

CFLAGS_append_libc-musl = " -D__error_t_defined "
do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
	rm -f ${S}/m4/libassuan.m4
	rm -f ${S}/m4/python.m4
}

do_install_append() {
       oe_multilib_header gpgme.h
}
