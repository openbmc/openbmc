SUMMARY = "A suite for man in the middle attacks"
HOMEPAGE = "https://github.com/Ettercap/ettercap"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit cmake

DEPENDS += "ethtool \
            geoip \
            librepo \
            libnet \
            libpcap \
            ncurses \
            openssl \
            zlib \
            bison-native \
            flex-native \
            "

RDEPENDS_${PN} += "bash ethtool libgcc"

SRC_URI = "gitsm://github.com/Ettercap/ettercap"

SRCREV = "7281fbddb7da7478beb1d21e3cb105fff3778b31"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = " \
    -DCMAKE_SKIP_RPATH=TRUE \
    -DBUNDLED_LIBS=ON \
    -DENABLE_IPV6=ON \
    -DENABLE_GTK=OFF \
"

# Replaces default encoding set (ISO-8859-1) with UTF-8 in ettercap
# configuration file installed by the package.
# It ensures that all characters are properly decoded and avoids
# any fatal errors while running in text mode (-T).
do_install_append() {
        sed -i 's@utf8_encoding.*@utf8_encoding = "UTF-8"@g' \
		${D}/etc/ettercap/etter.conf
}
