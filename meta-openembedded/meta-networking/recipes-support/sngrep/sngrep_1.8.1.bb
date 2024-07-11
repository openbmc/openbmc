SUMMARY = "Ncurses SIP Messages flow viewer"
DESCRIPTION = "Tool for displaying SIP calls message flows from terminal"
HOMEPAGE = "https://github.com/irontec/sngrep"
BUGTRACKER = "https://github.com/irontec/sngrep/issues"
SECTION = "console/network"
LICENSE = "GPL-3.0-or-later & OpenSSL"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE.OpenSSL;md5=e39170c41c6f83de36426dbf49a03632 \
    file://README;beginline=100;endline=124;md5=758a88cf2b27572df05996a3810066b3 \
"

DEPENDS = "\
    libpcap \
    ncurses \
"

SRC_URI = "git://github.com/irontec/sngrep.git;protocol=https;branch=master"
SRCREV = "373abb90804ba71f980c7120e62f90d3a5c81213"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGECONFIG ?= "\
    openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
    unicode \
"

PACKAGECONFIG[openssl] = "-DWITH_OPENSSL=ON,-DWITH_OPENSSL=OFF,openssl"
PACKAGECONFIG[gnutls] = "-DWITH_GNUTLS=ON,-DWITH_GNUTLS=OFF,gnutls libgcrypt"
PACKAGECONFIG[pcre] = "-DWITH_PCRE=OFF,-DWITH_PCRE=OFF,libpcre"
PACKAGECONFIG[zlib] = "-DWITH_ZLIB=ON,-DWITH_ZLIB=OFF,zlib"
PACKAGECONFIG[unicode] = "-DWITH_UNICODE=ON,-DWITH_UNICODE=OFF"
PACKAGECONFIG[ipv6] = "-DUSE_IPV6=ON,-DUSE_IPV6=OFF"
PACKAGECONFIG[eep] = "-DUSE_EEP=ON,-DUSE_EEP=OFF"
