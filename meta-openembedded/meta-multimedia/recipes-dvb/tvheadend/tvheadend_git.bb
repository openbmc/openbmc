SUMMARY = "Tvheadend: TV streaming server and recorder"
HOMEPAGE = "https://tvheadend.org/"

inherit autotools-brokensep gettext gitpkgv pkgconfig

DEPENDS = "avahi cmake-native dvb-apps libdvbcsa libpcre2 openssl uriparser zlib"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9cae5acac2e9ee2fc3aec01ac88ce5db"

SRC_URI = "git://github.com/tvheadend/tvheadend.git;branch=master;protocol=https \
           file://0001-adjust-for-64bit-time_t.patch \
           "

SRCREV = "cc602833684953fc3e6f1c89d4f08f6dfef179e3"
PV = "4.3+git"
PKGV = "4.3+git${GITPKGV}"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--arch=${TARGET_ARCH} \
                 --disable-hdhomerun_static \
                 --disable-ffmpeg_static \
                 --disable-libav \
                 --python=python3 \
                 --disable-dvbscan \
                 "

EXTRA_OECONF:append:libc-musl = " --disable-execinfo"

EXTRA_OEMAKE = "CFLAGS_NO_WERROR=yes"
CLEANBROKEN = "1"

do_configure:append() {
    sed -i -e "s|${WORKDIR}|<TOPDIR>|g" ${B}/build.linux/build.c
}
