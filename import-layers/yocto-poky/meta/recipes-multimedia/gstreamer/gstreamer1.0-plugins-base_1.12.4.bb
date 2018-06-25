require gstreamer1.0-plugins-base.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=c54ce9345727175ff66d17b67ff51f58 \
                    file://COPYING.LIB;md5=6762ed442b3822387a51c92d928ead0d \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=a4e1830fce078028c8f0974161272607"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gst-plugins-base/gst-plugins-base-${PV}.tar.xz \
    file://get-caps-from-src-pad-when-query-caps.patch \
    file://0003-ssaparse-enhance-SSA-text-lines-parsing.patch \
    file://0004-subparse-set-need_segment-after-sink-pad-received-GS.patch \
    file://make-gio_unix_2_0-dependency-configurable.patch \
    file://0001-introspection.m4-prefix-pkgconfig-paths-with-PKG_CON.patch \
"
SRC_URI[md5sum] = "b35b04071f69b8ab69a7f3a497ff0131"
SRC_URI[sha256sum] = "4c306b03df0212f1b8903784e29bb3493319ba19ebebf13b0c56a17870292282"

S = "${WORKDIR}/gst-plugins-base-${PV}"
