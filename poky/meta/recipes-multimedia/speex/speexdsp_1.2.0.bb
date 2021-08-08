SUMMARY = "A patent-free DSP library"
DESCRIPTION = "SpeexDSP is a patent-free, Open Source/Free Software DSP library."
HOMEPAGE = "http://www.speex.org"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=314649d8ba9dd7045dfb6683f298d0a8"

SRC_URI = "http://downloads.xiph.org/releases/speex/speexdsp-${PV}.tar.gz"

UPSTREAM_CHECK_REGEX = "speexdsp-(?P<pver>\d+(\.\d+)+)\.tar"

SRC_URI[md5sum] = "b722df341576dc185d897131321008fc"
SRC_URI[sha256sum] = "682042fc6f9bee6294ec453f470dadc26c6ff29b9c9e9ad2ffc1f4312fd64771"

inherit autotools pkgconfig

EXTRA_OECONF = "\
        --disable-examples \
        ${@bb.utils.contains('TARGET_FPU', 'soft', '--enable-fixed-point --disable-float-api', '', d)} \
"

# speexdsp was split off from speex in 1.2rc2. Older versions of speex can't
# be installed together with speexdsp, since they contain overlapping files.
RCONFLICTS:${PN} = "speex (< 1.2rc2)"
RCONFLICTS:${PN}-dbg = "speex-dbg (< 1.2rc2)"
RCONFLICTS:${PN}-dev = "speex-dev (< 1.2rc2)"
RCONFLICTS:${PN}-staticdev = "speex-staticdev (< 1.2rc2)"
