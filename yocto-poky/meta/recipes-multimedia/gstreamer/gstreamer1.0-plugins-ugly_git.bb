DEFAULT_PREFERENCE = "-1"

include gstreamer1.0-plugins-ugly.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068"

SRC_URI = " \
    git://anongit.freedesktop.org/gstreamer/gst-plugins-ugly;name=base \
    git://anongit.freedesktop.org/gstreamer/common;destsuffix=git/common;name=common \
"

PV = "1.7.2+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

SRCREV_base = "8bdb68edbc605e21314b608e7a39bdbaab7302b8"
SRCREV_common = "b64f03f6090245624608beb5d2fff335e23a01c0"
SRCREV_FORMAT = "base"

S = "${WORKDIR}/git"

# The mpg123 plugin was added prior to the 1.7.2 release
# https://cgit.freedesktop.org/gstreamer/gst-plugins-ugly/commit/?id=43bd45ba991ef3247957ca37cdcb52f4b8c0acb1
PACKAGECONFIG[mpg123] = "--enable-mpg123,--disable-mpg123,mpg123"

do_configure_prepend() {
	${S}/autogen.sh --noconfigure
}

# In 1.7.2, the mpg123 plugin was moved from -bad to -ugly
# https://cgit.freedesktop.org/gstreamer/gst-plugins-bad/commit/?id=08d8aefcdaaf89ecb6dd53ec1e4f95cd42d01664
# https://cgit.freedesktop.org/gstreamer/gst-plugins-ugly/commit/?id=43bd45ba991ef3247957ca37cdcb52f4b8c0acb1

PACKAGESPLITFUNCS_append = " handle_mpg123_rename "

python handle_mpg123_rename () {
    d.setVar('RPROVIDES_gstreamer1.0-plugins-ugly-mpg123', 'gstreamer1.0-plugins-bad-mpg123')
    d.setVar('RREPLACES_gstreamer1.0-plugins-ugly-mpg123', 'gstreamer1.0-plugins-bad-mpg123')
    d.setVar('RCONFLICTS_gstreamer1.0-plugins-ugly-mpg123', 'gstreamer1.0-plugins-bad-mpg123')
}
