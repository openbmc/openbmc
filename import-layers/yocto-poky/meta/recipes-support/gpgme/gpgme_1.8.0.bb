SUMMARY = "High-level GnuPG encryption/signing API"
DESCRIPTION = "GnuPG Made Easy (GPGME) is a library designed to make access to GnuPG easier for applications. It provides a High-Level Crypto API for encryption, decryption, signing, signature verification and key management"
HOMEPAGE = "http://www.gnupg.org/gpgme.html"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LESSER;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://src/gpgme.h.in;endline=23;md5=0f7059665c4b7897f4f4d0cb93aa9f98 \
                    file://src/engine.h;endline=22;md5=4b6d8ba313d9b564cc4d4cfb1640af9d"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/gpgme/${BP}.tar.bz2 \
           file://pkgconfig.patch \
           file://python-lang-config.patch \
           file://0001-Correctly-install-python-modules.patch \
           file://python-import.patch \
           file://0001-gpgme-config-skip-all-lib-or-usr-lib-directories-in-.patch \
          "

SRC_URI[md5sum] = "722a4153904b9b5dc15485a22d29263b"
SRC_URI[sha256sum] = "596097257c2ce22e747741f8ff3d7e24f6e26231fa198a41b2a072e62d1e5d33"

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

EXTRA_OECONF += '--enable-languages="${LANGUAGES}"'

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
}
