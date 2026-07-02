SUMMARY = "GNU poke is an extensible editor for structured binary data"
HOMEPAGE = "https://pokology.org"
DESCRIPTION = "GNU poke is an interactive, extensible editor for binary data. Not limited to editing basic entities such as bits and bytes, it provides a full-fledged procedural, interactive programming language designed to describe data structures and to operate on them."
SECTION = "console/utils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/poke/poke-${PV}.tar.gz \
          file://0003-configure.ac-HELP2MAN-replace-by-true-when-cross-com.patch \
          "

DEPENDS = "flex-native bison-native bdwgc readline"

SRC_URI[sha256sum] = "a84cb9175d50d45a411f2481fd0662b83cb32ce517316b889cfb570819579373"

inherit autotools gettext pkgconfig

EXTRA_OECONF = "--disable-gui \
                --disable-libnbd \
                --with-libreadline-prefix=${STAGING_INCDIR} \
                "

# The bundled jitter sub-configure runs a gnulib AC_RUN_IFELSE test for
# strcasecmp which aborts under cross compilation; provide the cached result.
CACHED_CONFIGUREVARS += "gl_cv_func_strcasecmp_works=yes"

PACKAGECONFIG[mi] = "--enable-mi,--disable-mi,json-c"

PACKAGES =+ "${PN}-emacs ${PN}-vim"

# Disable parallel install as it is not supported upstream
PARALLEL_MAKEINST = "-j1"

FILES:${PN}-emacs += "${datadir}/emacs/site-lisp"
FILES:${PN}-vim += "${datadir}/vim/vimfiles"
