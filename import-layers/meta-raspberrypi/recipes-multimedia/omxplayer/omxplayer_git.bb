SUMMARY = "A commandline OMX player for the Raspberry Pi"
DESCRIPTION = "This player was developed as a testbed for the XBMC \
Raspberry PI implementation and is quite handy to use standalone"
HOMEPAGE = "https://github.com/popcornmix/omxplayer"
SECTION = "console/utils"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libpcre libav virtual/egl boost freetype dbus openssl samba libssh libomxil coreutils-native curl-native"
PR = "r4"

SRCREV_default = "b8ff59dccd9307f10dad71bec2525a95bd6c603b"

# omxplayer builds its own copy of ffmpeg from source instead of using the
# system's ffmpeg library. This isn't ideal but it's ok for now. We do however
# want to keep control of the exact version of ffmpeg used instead of just
# fetching the latest commit on a release branch (which is what the checkout job
# in Makefile.ffmpeg in the omxplayer source tree does).
#
# This SRCREV corresponds to the v3.1.10 release of ffmpeg.
SRCREV_ffmpeg = "afa34cb36edca0ff809b7e58474bbce12271ecba"

SRC_URI = "git://github.com/popcornmix/omxplayer.git;protocol=git;branch=master \
           git://source.ffmpeg.org/ffmpeg;branch=release/3.1;protocol=git;depth=1;name=ffmpeg;destsuffix=git/ffmpeg \
           file://0001-Remove-Makefile.include-which-includes-hardcoded.patch \
           file://0002-Libraries-and-headers-from-ffmpeg-are-installed-in-u.patch \
           file://0003-Remove-strip-step-in-Makefile.patch \
           file://0004-Add-FFMPEG_EXTRA_CFLAGS-and-FFMPEG_EXTRA_LDFLAGS.patch \
           file://fix-tar-command-with-DIST.patch \
           file://use-native-pkg-config.patch \
           file://0005-Don-t-require-internet-connection-during-build.patch \
           file://0006-Prevent-ffmpeg-configure-compile-race-condition.patch \
           "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE ?= "null"
COMPATIBLE_MACHINE_rpi_aarch64 = "null"
COMPATIBLE_MACHINE_rpi = "(.*)"

inherit autotools-brokensep pkgconfig

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
export INCLUDES = "-isystem${STAGING_DIR_HOST}/usr/include/interface/vcos/pthreads \
                   -isystem${STAGING_DIR_HOST}/usr/include/freetype2 \
                   -isystem${STAGING_DIR_HOST}/usr/include/interface/vmcs_host/linux \
                   -isystem${STAGING_DIR_HOST}/usr/include/dbus-1.0 \
                   -isystem${STAGING_DIR_HOST}/usr/lib/dbus-1.0/include \
                  "
export DIST = "${D}"

do_compile() {
    # Needed for compiler test in ffmpeg's configure
    mkdir -p tmp

    oe_runmake -f Makefile.ffmpeg
    oe_runmake -f Makefile.ffmpeg install
    oe_runmake
}

do_install() {
    oe_runmake STRIP='echo skipping strip' dist
    mkdir -p ${D}${datadir}/fonts/truetype/freefont/
    install ${S}/fonts/* ${D}${datadir}/fonts/truetype/freefont/
}

FILES_${PN} = "${bindir}/omxplayer* \
               ${libdir}/omxplayer/lib*${SOLIBS} \
               ${datadir}/fonts"

FILES_${PN}-dev += "${libdir}/omxplayer/*.so"

RDEPENDS_${PN} += "bash procps"
