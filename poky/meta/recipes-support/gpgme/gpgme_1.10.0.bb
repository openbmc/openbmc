SUMMARY = "High-level GnuPG encryption/signing API"
DESCRIPTION = "GnuPG Made Easy (GPGME) is a library designed to make access to GnuPG easier for applications. It provides a High-Level Crypto API for encryption, decryption, signing, signature verification and key management"
HOMEPAGE = "http://www.gnupg.org/gpgme.html"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LESSER;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://src/gpgme.h.in;endline=23;md5=9d157d08a69059344e6f82abd2d25781 \
                    file://src/engine.h;endline=22;md5=4b6d8ba313d9b564cc4d4cfb1640af9d"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/gpgme/${BP}.tar.bz2 \
           file://0001-pkgconfig.patch \
           file://0002-gpgme-lang-python-gpg-error-config-should-not-be-use.patch \
           file://0003-Correctly-install-python-modules.patch \
           file://0004-python-import.patch \
           file://0005-gpgme-config-skip-all-lib-or-usr-lib-directories-in-.patch \
           file://0006-fix-build-path-issue.patch \
           file://0007-qt-python-Add-variables-to-tests.patch \
          "

SRC_URI[md5sum] = "78b1533c593478982ee2fc548260c563"
SRC_URI[sha256sum] = "1a8fed1197c3b99c35f403066bb344a26224d292afc048cfdfc4ccd5690a0693"

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
LANGUAGES ?= "${DEFAULT_LANGUAGES}"
LANGUAGES .= "${@bb.utils.contains('PACKAGECONFIG', 'python2', ' python2', '', d)}"
LANGUAGES .= "${@bb.utils.contains('PACKAGECONFIG', 'python3', ' python3', '', d)}"

PYTHON_INHERIT = "${@bb.utils.contains('PACKAGECONFIG', 'python2', 'pythonnative', '', d)}"
PYTHON_INHERIT .= "${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native', '', d)}"

EXTRA_OECONF += '--enable-languages="${LANGUAGES}" \
                 --disable-gpgconf-test \
                 --disable-gpg-test \
                 --disable-gpgsm-test \
                 --disable-g13-test \
                 --disable-lang-qt-test \
                 --disable-lang-python-test \
'

inherit autotools texinfo binconfig-disabled pkgconfig ${PYTHON_INHERIT}

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
