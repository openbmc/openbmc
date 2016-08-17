SUMMARY = "A patent-free DSP library"
DESCRIPTION = "SpeexDSP is a patent-free, Open Source/Free Software DSP library."
HOMEPAGE = "http://www.speex.org"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=314649d8ba9dd7045dfb6683f298d0a8"

SRC_URI = "http://downloads.xiph.org/releases/speex/speexdsp-${PV}.tar.gz \
           file://0001-Don-t-rely-on-HAVE_STDINT_H-et-al.-being-defined.patch"

SRC_URI[md5sum] = "70d9d31184f7eb761192fd1ef0b73333"
SRC_URI[sha256sum] = "4ae688600039f5d224bdf2e222d2fbde65608447e4c2f681585e4dca6df692f1"

inherit autotools pkgconfig

EXTRA_OECONF = "\
        --disable-examples \
        ${@bb.utils.contains('TARGET_FPU', 'soft', '--enable-fixed-point --disable-float-api', '', d)} \
"

# Workaround for a build failure when building with MACHINE=qemuarm64. I think
# aarch64 is supposed to support NEON just fine, but building for qemuarm64
# fails in NEON code:
#
# .../speexdsp-1.2rc3/libspeexdsp/resample_neon.h:148:5: error: impossible constraint in 'asm'
#      asm volatile ("  cmp %[len], #0\n"
#      ^
#
# I sent an email about the issue to speex-dev. At the time of writing there
# are no responses yet:
# http://thread.gmane.org/gmane.comp.audio.compression.speex.devel/7360
EXTRA_OECONF += "${@bb.utils.contains('TUNE_FEATURES', 'aarch64', '--disable-neon', '', d)}"

# speexdsp was split off from speex in 1.2rc2. Older versions of speex can't
# be installed together with speexdsp, since they contain overlapping files.
RCONFLICTS_${PN} = "speex (< 1.2rc2)"
RCONFLICTS_${PN}-dbg = "speex-dbg (< 1.2rc2)"
RCONFLICTS_${PN}-dev = "speex-dev (< 1.2rc2)"
RCONFLICTS_${PN}-staticdev = "speex-staticdev (< 1.2rc2)"
