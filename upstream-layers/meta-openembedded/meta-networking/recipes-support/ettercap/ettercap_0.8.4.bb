SUMMARY = "A suite for man in the middle attacks"
HOMEPAGE = "https://github.com/Ettercap/ettercap"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit cmake

DEPENDS += "ethtool \
            geoip \
            libmaxminddb \
            librepo \
            libnet \
            libpcap \
            libpcre \
            ncurses \
            openssl \
            zlib \
            bison-native \
            flex-native \
            "

RDEPENDS:${PN} += "bash ethtool libgcc"

SRC_URI = "gitsm://github.com/Ettercap/ettercap;branch=master;protocol=https;tag=v${PV}"

SRCREV = "41da65f4026a9e4cea928e61941b976d9279f508"

EXTRA_OECMAKE = " \
    -DCMAKE_SKIP_RPATH=TRUE \
    -DBUNDLED_LIBS=ON \
    -DENABLE_IPV6=ON \
    -DENABLE_GTK=OFF \
    -DFLEX_TARGET_ARG_COMPILE_FLAGS='--noline' \
    -DBISON_TARGET_ARG_COMPILE_FLAGS='--no-lines' \
"

CFLAGS += "-D_GNU_SOURCE"
# Replaces default encoding set (ISO-8859-1) with UTF-8 in ettercap
# configuration file installed by the package.
# It ensures that all characters are properly decoded and avoids
# any fatal errors while running in text mode (-T).
do_install:append() {
        sed -i 's@utf8_encoding.*@utf8_encoding = "UTF-8"@g' \
		${D}/etc/ettercap/etter.conf
}
