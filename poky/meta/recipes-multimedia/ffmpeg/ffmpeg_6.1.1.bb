SUMMARY = "A complete, cross-platform solution to record, convert and stream audio and video."
DESCRIPTION = "FFmpeg is the leading multimedia framework, able to decode, encode, transcode, \
               mux, demux, stream, filter and play pretty much anything that humans and machines \
               have created. It supports the most obscure ancient formats up to the cutting edge."
HOMEPAGE = "https://www.ffmpeg.org/"
SECTION = "libs"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & ISC & MIT & BSD-2-Clause & BSD-3-Clause & IJG"
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:libavcodec = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE:libavdevice = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE:libavfilter = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE:libavformat = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE:libavutil = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE:libpostproc = "GPL-2.0-or-later"
LICENSE:libswresample = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE:libswscale = "${@bb.utils.contains('PACKAGECONFIG', 'gpl', 'GPL-2.0-or-later', 'LGPL-2.1-or-later', d)}"
LICENSE_FLAGS = "commercial"

LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=bd7a443320af8c812e4c18d1b79df004 \
                    file://COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = " \
    https://www.ffmpeg.org/releases/${BP}.tar.xz \
    file://av1_ordering_info.patch \
    file://vulkan_av1_stable_API.patch \
    file://vulkan_fix_gcc14.patch \
"

SRC_URI[sha256sum] = "8684f4b00f94b85461884c3719382f1261f0d9eb3d59640a1f4ac0873616f968"

# https://nvd.nist.gov/vuln/detail/CVE-2023-39018
# https://github.com/bramp/ffmpeg-cli-wrapper/issues/291
# https://security-tracker.debian.org/tracker/CVE-2023-39018
# https://bugzilla.suse.com/show_bug.cgi?id=CVE-2023-39018
CVE_STATUS[CVE-2023-39018] = "cpe-incorrect: This issue belongs to ffmpeg-cli-wrapper \
(Java wrapper around the FFmpeg CLI) and not ffmepg itself."

# Build fails when thumb is enabled: https://bugzilla.yoctoproject.org/show_bug.cgi?id=7717
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

# Should be API compatible with libav (which was a fork of ffmpeg)
# libpostproc was previously packaged from a separate recipe
PROVIDES = "libav libpostproc"

DEPENDS = "nasm-native"

inherit autotools pkgconfig

PACKAGECONFIG ??= "avdevice avfilter avcodec avformat swresample swscale postproc \
                   alsa bzlib lzma theora zlib \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xv xcb', '', d)}"

# libraries to build in addition to avutil
PACKAGECONFIG[avdevice] = "--enable-avdevice,--disable-avdevice"
PACKAGECONFIG[avfilter] = "--enable-avfilter,--disable-avfilter"
PACKAGECONFIG[avcodec] = "--enable-avcodec,--disable-avcodec"
PACKAGECONFIG[avformat] = "--enable-avformat,--disable-avformat"
PACKAGECONFIG[swresample] = "--enable-swresample,--disable-swresample"
PACKAGECONFIG[swscale] = "--enable-swscale,--disable-swscale"
PACKAGECONFIG[postproc] = "--enable-postproc,--disable-postproc"

# features to support
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[altivec] = "--enable-altivec,--disable-altivec,"
PACKAGECONFIG[bzlib] = "--enable-bzlib,--disable-bzlib,bzip2"
PACKAGECONFIG[fdk-aac] = "--enable-libfdk-aac --enable-nonfree,--disable-libfdk-aac,fdk-aac"
PACKAGECONFIG[gpl] = "--enable-gpl,--disable-gpl"
PACKAGECONFIG[gsm] = "--enable-libgsm,--disable-libgsm,libgsm"
PACKAGECONFIG[jack] = "--enable-indev=jack,--disable-indev=jack,jack"
PACKAGECONFIG[libopus] = "--enable-libopus,--disable-libopus,libopus"
PACKAGECONFIG[libvorbis] = "--enable-libvorbis,--disable-libvorbis,libvorbis"
PACKAGECONFIG[lzma] = "--enable-lzma,--disable-lzma,xz"
PACKAGECONFIG[mfx] = "--enable-libmfx,--disable-libmfx,intel-mediasdk"
PACKAGECONFIG[mp3lame] = "--enable-libmp3lame,--disable-libmp3lame,lame"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[sdl2] = "--enable-sdl2,--disable-sdl2,virtual/libsdl2"
PACKAGECONFIG[speex] = "--enable-libspeex,--disable-libspeex,speex"
PACKAGECONFIG[srt] = "--enable-libsrt,--disable-libsrt,srt"
PACKAGECONFIG[theora] = "--enable-libtheora,--disable-libtheora,libtheora libogg"
PACKAGECONFIG[v4l2] = "--enable-libv4l2,--disable-libv4l2,v4l-utils"
PACKAGECONFIG[vaapi] = "--enable-vaapi,--disable-vaapi,libva"
PACKAGECONFIG[vdpau] = "--enable-vdpau,--disable-vdpau,libvdpau"
PACKAGECONFIG[vpx] = "--enable-libvpx,--disable-libvpx,libvpx"
PACKAGECONFIG[x264] = "--enable-libx264,--disable-libx264,x264"
PACKAGECONFIG[x265] = "--enable-libx265,--disable-libx265,x265"
PACKAGECONFIG[xcb] = "--enable-libxcb,--disable-libxcb,libxcb"
PACKAGECONFIG[xv] = "--enable-outdev=xv,--disable-outdev=xv,libxv"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib"

# Check codecs that require --enable-nonfree
USE_NONFREE = "${@bb.utils.contains_any('PACKAGECONFIG', [ 'openssl' ], 'yes', '', d)}"

def cpu(d):
    for arg in (d.getVar('TUNE_CCARGS') or '').split():
        if arg.startswith('-mcpu='):
            return arg[6:]
    return 'generic'

EXTRA_OECONF = " \
    --disable-stripping \
    --enable-pic \
    --enable-shared \
    --enable-pthreads \
    ${@bb.utils.contains('USE_NONFREE', 'yes', '--enable-nonfree', '', d)} \
    \
    --cross-prefix=${TARGET_PREFIX} \
    \
    --ld='${CCLD}' \
    --cc='${CC}' \
    --cxx='${CXX}' \
    --arch=${TARGET_ARCH} \
    --target-os='linux' \
    --enable-cross-compile \
    --extra-cflags='${CFLAGS} ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}' \
    --extra-ldflags='${LDFLAGS}' \
    --sysroot='${STAGING_DIR_TARGET}' \
    ${EXTRA_FFCONF} \
    --libdir=${libdir} \
    --shlibdir=${libdir} \
    --datadir=${datadir}/ffmpeg \
    ${@bb.utils.contains('AVAILTUNES', 'mips32r2', '', '--disable-mipsdsp --disable-mipsdspr2', d)} \
    --cpu=${@cpu(d)} \
    --pkg-config=pkg-config \
"

EXTRA_OECONF:append:linux-gnux32 = " --disable-asm"

EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'mipsisa64r6', '--disable-mips64r2 --disable-mips32r2', '', d)}"
EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'mipsisa64r2', '--disable-mips64r6 --disable-mips32r6', '', d)}"
EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'mips32r2', '--disable-mips64r6 --disable-mips32r6', '', d)}"
EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'mips32r6', '--disable-mips64r2 --disable-mips32r2', '', d)}"
EXTRA_OECONF:append:mips = " --extra-libs=-latomic --disable-mips32r5 --disable-mipsdsp --disable-mipsdspr2 \
                             --disable-loongson2 --disable-loongson3 --disable-mmi --disable-msa"
EXTRA_OECONF:append:riscv32 = " --extra-libs=-latomic --disable-rvv --disable-asm"
EXTRA_OECONF:append:armv5 = " --extra-libs=-latomic"
EXTRA_OECONF:append:powerpc = " --extra-libs=-latomic"
EXTRA_OECONF:append:armv7a = "${@bb.utils.contains('TUNE_FEATURES','neon','',' --disable-neon',d)}"
EXTRA_OECONF:append:armv7ve = "${@bb.utils.contains('TUNE_FEATURES','neon','',' --disable-neon',d)}"

# gold crashes on x86, another solution is to --disable-asm but thats more hacky
# ld.gold: internal error in relocate_section, at ../../gold/i386.cc:3684

LDFLAGS:append:x86 = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"
LDFLAGS:append:x86 = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' -fuse-ld=bfd ', '', d)}"

EXTRA_OEMAKE = "V=1"

do_configure() {
    export TMPDIR="${B}/tmp"
    mkdir -p ${B}/tmp
    ${S}/configure ${EXTRA_OECONF}
    sed -i -e "s,^X86ASMFLAGS=.*,& --debug-prefix-map=${S}=${TARGET_DBGSRC_DIR} --debug-prefix-map=${B}=${TARGET_DBGSRC_DIR},g" ${B}/ffbuild/config.mak
}

# patch out build host paths for reproducibility
do_compile:prepend:class-target() {
        sed -i -e "s,${WORKDIR},,g" ${B}/config.h
}

PACKAGES =+ "libavcodec \
             libavdevice \
             libavfilter \
             libavformat \
             libavutil \
             libpostproc \
             libswresample \
             libswscale"

FILES:libavcodec = "${libdir}/libavcodec${SOLIBS}"
FILES:libavdevice = "${libdir}/libavdevice${SOLIBS}"
FILES:libavfilter = "${libdir}/libavfilter${SOLIBS}"
FILES:libavformat = "${libdir}/libavformat${SOLIBS}"
FILES:libavutil = "${libdir}/libavutil${SOLIBS}"
FILES:libpostproc = "${libdir}/libpostproc${SOLIBS}"
FILES:libswresample = "${libdir}/libswresample${SOLIBS}"
FILES:libswscale = "${libdir}/libswscale${SOLIBS}"

# ffmpeg disables PIC on some platforms (e.g. x86-32)
INSANE_SKIP:${MLPREFIX}libavcodec = "textrel"
INSANE_SKIP:${MLPREFIX}libavdevice = "textrel"
INSANE_SKIP:${MLPREFIX}libavfilter = "textrel"
INSANE_SKIP:${MLPREFIX}libavformat = "textrel"
INSANE_SKIP:${MLPREFIX}libavutil = "textrel"
INSANE_SKIP:${MLPREFIX}libswscale = "textrel"
INSANE_SKIP:${MLPREFIX}libswresample = "textrel"
INSANE_SKIP:${MLPREFIX}libpostproc = "textrel"
