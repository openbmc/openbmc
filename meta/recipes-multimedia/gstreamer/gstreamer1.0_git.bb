DEFAULT_PREFERENCE = "-1"

include gstreamer1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

SRC_URI = " \
    git://anongit.freedesktop.org/gstreamer/gstreamer;name=base \
    git://anongit.freedesktop.org/gstreamer/common;destsuffix=git/common;name=common \
"

PV = "1.7.2+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

SRCREV_base = "9e33bfa2c7a5f43da2c49b0a8235fd43cba9feaf"
SRCREV_common = "b64f03f6090245624608beb5d2fff335e23a01c0"
SRCREV_FORMAT = "base"

S = "${WORKDIR}/git"

# The option to configure tracer hooks was added prior to the 1.7.2 release
# https://cgit.freedesktop.org/gstreamer/gstreamer/commit/?id=e5ca47236e4df4683707f0bcf99181a937d358d5
PACKAGECONFIG[gst-tracer-hooks] = "--enable-gst-tracer-hooks,--disable-gst-tracer-hooks,"
PACKAGECONFIG[trace-historic] = "--enable-trace,--disable-trace,"

do_configure_prepend() {
	${S}/autogen.sh --noconfigure
}
