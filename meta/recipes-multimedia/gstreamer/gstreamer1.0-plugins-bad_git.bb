DEFAULT_PREFERENCE = "-1"

include gstreamer1.0-plugins-bad.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://gst/tta/crc32.h;beginline=12;endline=29;md5=27db269c575d1e5317fffca2d33b3b50 \
                    file://gst/tta/filters.h;beginline=12;endline=29;md5=8a08270656f2f8ad7bb3655b83138e5a"

SRC_URI = " \
    git://anongit.freedesktop.org/gstreamer/gst-plugins-bad;name=base \
    git://anongit.freedesktop.org/gstreamer/common;destsuffix=git/common;name=common \
    file://configure-allow-to-disable-libssh2.patch \
    file://fix-maybe-uninitialized-warnings-when-compiling-with-Os.patch \
    file://avoid-including-sys-poll.h-directly.patch \
    file://ensure-valid-sentinels-for-gst_structure_get-etc.patch \
    file://0001-gstreamer-gl.pc.in-don-t-append-GL_CFLAGS-to-CFLAGS.patch \
"

PV = "1.7.2+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

SRCREV_base = "50ae46cc0f8827bf966920d9c221e5cf86e811ba"
SRCREV_common = "a25397448942079002622be231e9ec49b985745a"
SRCREV_FORMAT = "base"

S = "${WORKDIR}/git"

# over-ride the default hls PACKAGECONFIG in gstreamer1.0-plugins-bad.inc to
# pass an additional --with-hls-crypto=XXX option (new in 1.7.x) and switch HLS
# AES decryption from nettle to openssl (ie a shared dependency with dtls).
# This should move back to the common .inc once the main recipe updates to 1.8.x
PACKAGECONFIG[hls] = "--enable-hls --with-hls-crypto=openssl,--disable-hls,openssl"

# The tinyalsa plugin was added prior to the 1.7.2 release
# https://cgit.freedesktop.org/gstreamer/gst-plugins-bad/commit/?id=c8bd74fa9a81398f57d976c478d2043f30188684
PACKAGECONFIG[tinyalsa] = "--enable-tinyalsa,--disable-tinyalsa,tinyalsa"

# The vulkan based video sink plugin was added prior to the 1.7.2 release
# https://cgit.freedesktop.org/gstreamer/gst-plugins-bad/commit/?id=5de6dd9f40629562acf90e35e1fa58464d66617d
PACKAGECONFIG[vulkan] = "--enable-vulkan,--disable-vulkan,libxcb"

# The dependency-less netsim plugin was added prior to the 1.7.2 release
# https://cgit.freedesktop.org/gstreamer/gst-plugins-bad/commit/?id=e3f9e854f08e82bfab11182c5a2aa6f9a0c73cd5
EXTRA_OECONF += " \
    --enable-netsim \
"

do_configure_prepend() {
	${S}/autogen.sh --noconfigure
}

# In 1.6.2, the "--enable-hls" configure option generated an installable package
# called "gstreamer1.0-plugins-bad-fragmented". In 1.7.1 that HLS plugin package
# has become "gstreamer1.0-plugins-bad-hls". See:
# http://cgit.freedesktop.org/gstreamer/gst-plugins-bad/commit/?id=efe62292a3d045126654d93239fdf4cc8e48ae08

PACKAGESPLITFUNCS_append = " handle_hls_rename "

python handle_hls_rename () {
    d.setVar('RPROVIDES_gstreamer1.0-plugins-bad-hls', 'gstreamer1.0-plugins-bad-fragmented')
    d.setVar('RREPLACES_gstreamer1.0-plugins-bad-hls', 'gstreamer1.0-plugins-bad-fragmented')
    d.setVar('RCONFLICTS_gstreamer1.0-plugins-bad-hls', 'gstreamer1.0-plugins-bad-fragmented')
}
