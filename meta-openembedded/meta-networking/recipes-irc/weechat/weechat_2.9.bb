SUMMARY = "Full-featured IRC plugin: multi-servers, proxy support, IPv6, SASL authentication, nicklist, DCC, and many other features"
HOMEPAE = "https://weechat.org/"
SECTION = "net"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

DEPENDS = "openssl zlib icu libgcrypt gnutls curl aspell"

SRC_URI = "https://weechat.org/files/src/weechat-${PV}.tar.xz \
           file://0001-use-pkg-config-for-gcrypt-instead.patch \
           "

SRC_URI[sha256sum] = "eab406c385c3a10d0107ddc3aac6596ae8c59af99e9158c6d769e90ec9adfa0e"

inherit cmake pkgconfig

PACKAGECONFIG ??= " ncurses python"
PACKAGECONFIG[ncurses] = "-DENABLE_NCURSES=ON,-DENABLE_NCURSES=OFF,ncurses"
PACKAGECONFIG[python] = "-DENABLE_PYTHON=ON,-DENABLE_PYTHON=OFF,python3,python3"

EXTRA_OECMAKE_append = " -DENABLE_PHP=OFF -DENABLE_TCL=OFF -DENABLE_LUA=OFF -DENABLE_JAVASCRIPT=OFF -DENABLE_RUBY=OFF -DENABLE_GUILE=OFF -DENABLE_PERL=OFF -DENABLE_ASPELL=ON"

do_configure_prepend(){
    #  Make sure we get dependencies from recipe-sysroot
    sed -i -e 's# /usr/bin# ${RECIPE_SYSROOT}/${bindir}/#g' ${S}/cmake/FindPerl.cmake
    sed -i -e 's# /usr/local/bin##g' ${S}/cmake/FindPerl.cmake
    sed -i -e 's# /usr/pkg/bin##g' ${S}/cmake/FindPerl.cmake
}

do_install_append(){
    rm -rf ${D}/${datadir}
}

