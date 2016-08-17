SUMMARY = "Open Source multimedia player"
SECTION = "multimedia"
HOMEPAGE = "http://www.mplayerhq.hu/"
DEPENDS = "libvpx libdvdread libtheora virtual/libsdl ffmpeg xsp zlib \
           libpng jpeg liba52 freetype fontconfig alsa-lib lzo ncurses \
           libxv virtual/libx11 libass speex faad2 libxscrnsaver"

RDEPENDS_${PN} = "mplayer-common"
PROVIDES = "mplayer"
RPROVIDES_${PN} = "mplayer"
RCONFLICTS_${PN} = "mplayer"

# Depends on xsp, libxv, virtual/libx11, libxscrnsaver
REQUIRED_DISTRO_FEATURES = "x11"

# because it depends on libpostproc/libav which has commercial flag
LICENSE_FLAGS = "${@bb.utils.contains('PACKAGECONFIG', 'postproc', 'commercial', '', d)}"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://repo.or.cz/mplayer.git \
    file://0001-configure-don-t-disable-ASS-support-when-explicitly-.patch \
    file://0001-demux_ogg-partially-port-libtheora-glue-code-to-Theo.patch \
"

SRCREV = "2c378c71a4d9b1df382db9aa787b646628b4e3f9"

ARM_INSTRUCTION_SET = "arm"

PV = "2.0+gitr${SRCPV}"
PR = "r13"

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

FILES_${PN} = "${bindir}/mplayer ${libdir} /usr/etc/mplayer/"
CONFFILES_${PN} += "/usr/etc/mplayer/input.conf \
                    /usr/etc/mplayer/example.conf \
                    /usr/etc/mplayer/codecs.conf \
"

inherit autotools-brokensep pkgconfig python3native

EXTRA_OECONF = " \
    --prefix=/usr \
    --mandir=${mandir} \
    --target=${SIMPLE_TARGET_SYS} \
    \
    --disable-lirc \
    --disable-lircc \
    --disable-joystick \
    --disable-vm \
    --disable-xf86keysym \
    --enable-tv \
    --enable-tv-v4l2 \
    --disable-tv-bsdbt848 \
    --enable-rtc \
    --enable-networking \
    --disable-smb \
    --disable-dvdnav \
    --enable-dvdread \
    --disable-dvdread-internal \
    --disable-libdvdcss-internal \
    --disable-enca \
    --disable-ftp \
    --disable-vstream \
    \
    --disable-gif \
    --enable-png \
    --enable-jpeg \
    --disable-libcdio \
    --disable-qtx \
    --disable-xanim \
    --disable-real \
    --disable-xvid \
    \
    --enable-speex \
    --enable-theora \
    --disable-ladspa \
    --disable-libdv \
    --enable-mad \
    --disable-xmms \
    --disable-musepack \
    \
    --disable-gl \
    --enable-sdl \
    --disable-caca \
    --disable-directx \
    --disable-dvb \
    --enable-xv \
    --disable-vm \
    --disable-xinerama \
    --enable-x11 \
    --disable-directfb \
    --disable-tga \
    --disable-pnm \
    --disable-md5sum \
    \
    --enable-alsa \
    --enable-ossaudio \
    --disable-pulse \
    --disable-jack \
    --disable-openal \
    --enable-select \
    --enable-libass \
    \
    --extra-libs=' -lXext -lX11 -lvorbis -ltheoradec -lasound ' \
"
# -ltheoradec is missing in:
# libmpcodecs/vd_theora.o: undefined reference to symbol 'theora_decode_init@@libtheora.so.1.0'

EXTRA_OECONF_append_armv6 = " --enable-armv6"
EXTRA_OECONF_append_armv7a = " --enable-armv6 --enable-neon"

PACKAGECONFIG ??= "vorbis postproc"
PACKAGECONFIG[mad] = "--enable-mad,--disable-mad,libmad"
PACKAGECONFIG[a52] = "--enable-liba52,--disable-liba52,liba52"
PACKAGECONFIG[lame] = ",,lame"
PACKAGECONFIG[postproc] = ",--disable-libpostproc,libpostproc"
PACKAGECONFIG[vorbis] = ",--disable-libvorbis,libvorbis"
PACKAGECONFIG[portaudio] = ",--disable-portaudio,portaudio-v19"
PACKAGECONFIG[mpg123] = ",--disable-mpg123,mpg123"
PACKAGECONFIG[directfb] = "--enable-directfb,--disable-directfb,directfb"

FULL_OPTIMIZATION = "-fexpensive-optimizations -fomit-frame-pointer -frename-registers -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"

CFLAGS_append = " -I${S}/libdvdread4 "

do_configure() {
    sed -i 's|/usr/include|${STAGING_INCDIR}|g' ${S}/configure
    sed -i 's|/usr/lib|${STAGING_LIBDIR}|g' ${S}/configure
    sed -i 's|/usr/\S*include[\w/]*||g' ${S}/configure
    sed -i 's|/usr/\S*lib[\w/]*||g' ${S}/configure
    sed -i 's|_install_strip="-s"|_install_strip=""|g' ${S}/configure
    sed -i 's|HOST_CC|BUILD_CC|' ${S}/Makefile
    sed -i 's|extra_cflags="-I. $extra_cflags"|extra_cflags="-I. -I${STAGING_INCDIR}/directfb $extra_cflags"|g' ${S}/configure
    export SIMPLE_TARGET_SYS="$(echo ${TARGET_SYS} | sed s:${TARGET_VENDOR}::g)"
    ./configure ${EXTRA_OECONF}
    
}

do_compile () {
    oe_runmake
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install-no-man
    install -d ${D}/usr/etc/mplayer
    install ${S}/etc/input.conf ${D}/usr/etc/mplayer/
    install ${S}/etc/example.conf ${D}/usr/etc/mplayer/
    install ${S}/etc/codecs.conf ${D}/usr/etc/mplayer/
    [ -e ${D}/usr/lib ] && rmdir ${D}/usr/lib
}

# http://errors.yoctoproject.org/Errors/Details/40734/
PNBLACKLIST[mplayer2] ?= "Not compatible with currently used ffmpeg 3"
