SUMMARY = "Full-featured IRC plugin: multi-servers, proxy support, IPv6, SASL authentication, nicklist, DCC, and many other features"
HOMEPAE = "https://weechat.org/"
SECTION = "net"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = "openssl zlib icu libgcrypt gnutls curl aspell"

SRC_URI = "https://weechat.org/files/src/weechat-${PV}.tar.xz \
           file://0001-use-pkg-config-for-gcrypt-instead.patch \
           "

SRC_URI[sha256sum] = "7cd3dcc7029e888de49e13ebbcc3749586ff59c9d97f89f5eeb611067c7bb94c"

inherit cmake pkgconfig

PACKAGECONFIG ??= " ncurses python"
PACKAGECONFIG[ncurses] = "-DENABLE_NCURSES=ON,-DENABLE_NCURSES=OFF,ncurses"
PACKAGECONFIG[python] = "-DENABLE_PYTHON=ON,-DENABLE_PYTHON=OFF,python3,python3"

EXTRA_OECMAKE:append = " -DENABLE_PHP=OFF -DENABLE_TCL=OFF -DENABLE_LUA=OFF -DENABLE_JAVASCRIPT=OFF -DENABLE_RUBY=OFF -DENABLE_GUILE=OFF -DENABLE_PERL=OFF -DENABLE_ASPELL=ON"

do_configure:prepend(){
    #  Make sure we get dependencies from recipe-sysroot
    sed -i -e 's# /usr/bin# ${RECIPE_SYSROOT}/${bindir}/#g' ${S}/cmake/FindPerl.cmake
    sed -i -e 's# /usr/local/bin##g' ${S}/cmake/FindPerl.cmake
    sed -i -e 's# /usr/pkg/bin##g' ${S}/cmake/FindPerl.cmake
}

do_install:append(){
    rm -rf ${D}/${datadir}
}

