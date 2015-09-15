SUMMARY = "Open source audio and video processing tools and librairies"
DESCRIPTION = "Libav is a friendly and community-driven effort to provide its users \
               with a set of portable, functional and high-performance libraries for \
               dealing with multimedia formats of all sorts. It originates from the \
               FFmpeg codebase, but goes its own way these days, providing its users \
               with reliable releases and a clear vision how to go forward."
HOMEPAGE = "http://libav.org/"
SECTION = "libs"

LICENSE = "GPLv2+"
LICENSE_FLAGS = "commercial"

LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=bd7a443320af8c812e4c18d1b79df004 \
                    file://COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = "http://libav.org/releases/${BP}.tar.xz"

# Provides ffmpeg compat, see http://libav.org/about.html
PROVIDES = "ffmpeg"

ARM_INSTRUCTION_SET = "arm"

DEPENDS = "alsa-lib zlib libogg yasm-native"

inherit autotools pkgconfig

B = "${S}/build.${HOST_SYS}.${TARGET_SYS}"

FULL_OPTIMIZATION_armv7a = "-fexpensive-optimizations -fomit-frame-pointer -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"

EXTRA_FFCONF_armv7a = "--cpu=cortex-a8"
EXTRA_FFCONF ?= ""

PACKAGECONFIG ??= "bzip2 x264 theora ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[bzip2] = "--enable-bzlib,--disable-bzlib,bzip2"
PACKAGECONFIG[faac] = "--enable-libfaac,--disable-libfaac,faac"
PACKAGECONFIG[gsm] = "--enable-libgsm,--disable-libgsm,libgsm"
PACKAGECONFIG[jack] = "--enable-indev=jack,--disable-indev=jack,jack"
PACKAGECONFIG[libvorbis] = "--enable-libvorbis,--disable-libvorbis,libvorbis"
PACKAGECONFIG[mp3lame] = "--enable-libmp3lame,--disable-libmp3lame,lame"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[schroedinger] = "--enable-libschroedinger,--disable-libschroedinger,schroedinger"
PACKAGECONFIG[speex] = "--enable-libspeex,--disable-libspeex,speex"
PACKAGECONFIG[theora] = "--enable-libtheora,--disable-libtheora,libtheora"
PACKAGECONFIG[vaapi] = "--enable-vaapi,--disable-vaapi,libva"
PACKAGECONFIG[vpx] = "--enable-libvpx,--disable-libvpx,libvpx"
PACKAGECONFIG[x11] = "--enable-x11grab,--disable-x11grab,virtual/libx11 libxfixes libxext xproto virtual/libsdl"
PACKAGECONFIG[x264] = "--enable-libx264,--disable-libx264,x264"

# Check codecs that require --enable-nonfree
USE_NONFREE = "${@bb.utils.contains_any('PACKAGECONFIG', [ 'faac', 'openssl' ], 'yes', '', d)}"

EXTRA_OECONF = " \
    --enable-shared \
    --enable-pthreads \
    --enable-gpl \
    ${@bb.utils.contains('USE_NONFREE', 'yes', '--enable-nonfree', '', d)} \
    --enable-avfilter \
    \
    --cross-prefix=${TARGET_PREFIX} \
    --prefix=${prefix} \
    \
    --enable-avserver \
    --enable-avplay \
    --ld="${CCLD}" \
    --arch=${TARGET_ARCH} \
    --target-os="linux" \
    --enable-cross-compile \
    --extra-cflags="${TARGET_CFLAGS} ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}" \
    --extra-ldflags="${TARGET_LDFLAGS}" \
    --sysroot="${STAGING_DIR_TARGET}" \
    --enable-hardcoded-tables \
    ${EXTRA_FFCONF} \
    --libdir=${libdir} \
    --shlibdir=${libdir} \
"

do_configure() {
    # We don't have TARGET_PREFIX-pkgconfig
    sed -i '/pkg_config_default="${cross_prefix}${pkg_config_default}"/d' ${S}/configure
    mkdir -p ${B}
    cd ${B}
    ${S}/configure ${EXTRA_OECONF}
    sed -i -e s:Os:O4:g ${B}/config.h
}

do_install_append() {
    install -m 0644 ${S}/libavfilter/*.h ${D}${includedir}/libavfilter/
}

PACKAGES += "${PN}-vhook-dbg ${PN}-vhook ffmpeg-x264-presets"
PACKAGES_DYNAMIC += "^lib(av(codec|device|filter|format|util)|swscale).*"

RSUGGESTS_${PN} = "mplayer"
FILES_${PN} = "${bindir}"
FILES_${PN}-dev = "${includedir}/${PN}"

FILES_${PN}-vhook = "${libdir}/vhook"
FILES_${PN}-vhook-dbg += "${libdir}/vhook/.debug"

FILES_ffmpeg-x264-presets = "${datadir}/*.avpreset"

LEAD_SONAME = "libavcodec.so"

FILES_${PN}-dev = "${includedir}"

python populate_packages_prepend() {
    av_libdir = d.expand('${libdir}')
    av_pkgconfig = d.expand('${libdir}/pkgconfig')

    # Runtime package
    do_split_packages(d, av_libdir, '^lib(.*)\.so\..*',
                      output_pattern='lib%s',
                      description='libav %s library',
                      extra_depends='',
                      prepend=True,
                      allow_links=True)

    # Development packages (-dev, -staticdev)
    do_split_packages(d, av_libdir, '^lib(.*)\.so$',
                      output_pattern='lib%s-dev',
                      description='libav %s development package',
                      extra_depends='${PN}-dev',
                      prepend=True,
                      allow_links=True)
    do_split_packages(d, av_pkgconfig, '^lib(.*)\.pc$',
                      output_pattern='lib%s-dev',
                      description='libav %s development package',
                      extra_depends='${PN}-dev',
                      prepend=True)
    do_split_packages(d, av_libdir, '^lib(.*)\.a$',
                      output_pattern='lib%s-staticdev',
                      description='libav %s development package - static library',
                      extra_depends='${PN}-dev',
                      prepend=True,
                      allow_links=True)

    if d.getVar('TARGET_ARCH', True) in [ 'i586', 'i686' ]:
        # libav can't be build with -fPIC for 32-bit x86
        pkgs = d.getVar('PACKAGES', True).split()
        for pkg in pkgs:
            d.appendVar('INSANE_SKIP_%s' % pkg, ' textrel')
}
