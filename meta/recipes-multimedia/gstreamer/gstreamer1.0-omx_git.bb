DEFAULT_PREFERENCE = "-1"

include gstreamer1.0-omx.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=21;md5=5c8e1fca32704488e76d2ba9ddfa935f"

SRC_URI = " \
    git://anongit.freedesktop.org/gstreamer/gst-omx;branch=master;name=gst-omx \
    git://anongit.freedesktop.org/gstreamer/common;destsuffix=git/common;branch=master;name=common \
    file://0001-omx-fixed-type-error-in-printf-call.patch \
"

SRCREV_gst-omx = "a2db76b048db278ef0aa798e106b7594264e06c0"
SRCREV_common = "5edcd857b2107cd8b78c16232dd10877513ec157"

SRCREV_FORMAT = "gst-omx"

S = "${WORKDIR}/git"

do_configure_prepend() {
	cd ${S}
	./autogen.sh --noconfigure
	cd ${B}
}
