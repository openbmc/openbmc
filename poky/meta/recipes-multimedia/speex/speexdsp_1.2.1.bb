SUMMARY = "A patent-free DSP library"
DESCRIPTION = "SpeexDSP is a patent-free, Open Source/Free Software DSP library."
HOMEPAGE = "http://www.speex.org"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=eff3f76350f52a99a3df5eec6b79c02a"

SRC_URI = "http://downloads.xiph.org/releases/speex/speexdsp-${PV}.tar.gz"

UPSTREAM_CHECK_REGEX = "speexdsp-(?P<pver>\d+(\.\d+)+)\.tar"

SRC_URI[sha256sum] = "8c777343e4a6399569c72abc38a95b24db56882c83dbdb6c6424a5f4aeb54d3d"

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

BBCLASSEXTEND = "native"
