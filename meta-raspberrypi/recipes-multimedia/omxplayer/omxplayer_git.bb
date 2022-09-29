SUMMARY = "A commandline OMX player for the Raspberry Pi"
DESCRIPTION = "This player was developed as a testbed for the XBMC \
Raspberry PI implementation and is quite handy to use standalone"
HOMEPAGE = "https://github.com/popcornmix/omxplayer"
SECTION = "console/utils"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=00a27da7ac0f9bcd17320ec29ef4bbf6"

DEPENDS = "alsa-lib libpcre virtual/egl boost freetype dbus openssl libssh virtual/libomxil coreutils-native curl-native userland"

PR = "r6"

SRCREV_default = "1f1d0ccd65d3a1caa86dc79d2863a8f067c8e3f8"

# omxplayer builds its own copy of ffmpeg from source instead of using the
# system's ffmpeg library. This isn't ideal but it's ok for now. We do however
# want to keep control of the exact version of ffmpeg used instead of just
# fetching the latest commit on a release branch (which is what the checkout job
# in Makefile.ffmpeg in the omxplayer source tree does).
#
# This SRCREV corresponds to the v4.0.3 release of ffmpeg.
SRCREV_ffmpeg = "fcbd117df3077bad495e99e20f01cf93737bce76"

SRC_URI = "git://github.com/popcornmix/omxplayer.git;protocol=https;branch=master \
           git://github.com/FFmpeg/FFmpeg;branch=release/4.0;protocol=https;depth=1;name=ffmpeg;destsuffix=git/ffmpeg \
           file://0002-Libraries-and-headers-from-ffmpeg-are-installed-in-u.patch \
           file://0003-Remove-strip-step-in-Makefile.patch \
           file://0004-Add-FFMPEG_EXTRA_CFLAGS-and-FFMPEG_EXTRA_LDFLAGS.patch \
           file://fix-tar-command-with-DIST.patch \
           file://use-native-pkg-config.patch \
           file://0005-Don-t-require-internet-connection-during-build.patch \
           file://0006-Prevent-ffmpeg-configure-compile-race-condition.patch \
           file://0001-Specify-cc-cxx-and-ld-variables-from-environment.patch \
           file://cross-crompile-ffmpeg.patch \
           file://0007-Remove-Makefile-hardcoded-arch-tune.patch \
           "

SRC_URI:append = "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", " file://0001-Fix-build-with-vc4-driver.patch ", "", d)}"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "^rpi$"
COMPATIBLE_HOST:aarch64 = "null"

def cpu(d):
    for arg in (d.getVar('TUNE_CCARGS') or '').split():
        if arg.startswith('-mcpu='):
            return arg[6:]
    return 'generic'

export CPU = "${@cpu(d)}"

inherit autotools-brokensep pkgconfig

# This isn't used directly by omxplayer, but applied to Makefile.ffmpeg which
# runs the ffmpeg configuration
PACKAGECONFIG ??= ""
PACKAGECONFIG[samba] = "--enable-libsmbclient,--disable-libsmbclient,samba"

# Needed in ffmpeg configure
export TEMPDIR = "${S}/tmp"

# Needed in Makefile.ffmpeg
export HOST = "${HOST_SYS}"
export WORK = "${S}"
export FFMPEG_EXTRA_CFLAGS  = "${TUNE_CCARGS} ${TOOLCHAIN_OPTIONS}"
export FFMPEG_EXTRA_LDFLAGS  = "${TUNE_CCARGS} ${TOOLCHAIN_OPTIONS}"

# Needed in top Makefile

export LDFLAGS = "-L${S}/ffmpeg_compiled/usr/lib \
                  -L${STAGING_DIR_HOST}/lib \
                  -L${STAGING_DIR_HOST}/usr/lib \
                 "
export INCLUDES = "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", " -D__GBM__", "", d)} \
                   -isystem${STAGING_DIR_HOST}/usr/include/interface/vcos/pthreads \
                   -isystem${STAGING_DIR_HOST}/usr/include/freetype2 \
                   -isystem${STAGING_DIR_HOST}/usr/include/interface/vmcs_host/linux \
                   -isystem${STAGING_DIR_HOST}/usr/include/dbus-1.0 \
                   -isystem${STAGING_DIR_HOST}/usr/lib/dbus-1.0/include \
                  "
export DIST = "${D}"

do_compile() {
    bbwarn "omxplayer is being deprecated and resources are directed at improving vlc."

    # Needed for compiler test in ffmpeg's configure
    mkdir -p tmp

    sed -i 's/--enable-libsmbclient/${@bb.utils.contains("PACKAGECONFIG", "samba", "--enable-libsmbclient", "--disable-libsmbclient", d)}/g' Makefile.ffmpeg

    oe_runmake -f Makefile.ffmpeg
    oe_runmake -f Makefile.ffmpeg install
    oe_runmake
}

do_install() {
    oe_runmake STRIP='echo skipping strip' dist
    mkdir -p ${D}${datadir}/fonts/truetype/freefont/
    install ${S}/fonts/* ${D}${datadir}/fonts/truetype/freefont/
}

FILES:${PN} = "${bindir}/omxplayer* \
               ${libdir}/omxplayer/lib*${SOLIBS} \
               ${datadir}/fonts"

FILES:${PN}-dev += "${libdir}/omxplayer/*.so"

RDEPENDS:${PN} += "bash procps userland"
