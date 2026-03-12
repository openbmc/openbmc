SUMMARY = "A liberally licensed VNC server library"
DESCRIPTION = "This is a liberally licensed VNC server library that's intended to be fast and neat."
HOMEPAGE = "https://github.com/any1/neatvnc"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=94fc374e7174f41e3afe0f027ee59ff7"

SRC_URI = "git://github.com/any1/neatvnc;branch=v0.9;protocol=https \
           file://0001-meson-Use-new-pkgconfig-for-aml1.patch \
           file://0001-Add-method-to-listen-on-multiple-fds.patch \
           file://0001-Use-aml-v1.patch \
          "

SRCREV = "36ef59a83291368d72f471700702a8b6a76f763b"


DEPENDS = "libdrm pixman aml zlib"

PACKAGECONFIG ??= ""
PACKAGECONFIG[tls] = "-Dtls=enabled,-Dtls=disabled,gnutls"
PACKAGECONFIG[jpeg] = "-Djpeg=enabled,-Djpeg=disabled,libjpeg-turbo"
PACKAGECONFIG[examples] = "-Dexamples=true,-Dexamples=false,libpng"
PACKAGECONFIG[benchmarks] = "-Dbenchmarks=true,-Dbenchmarks=false,libpng"

PACKAGE_BEFORE_PN += "${PN}-examples"
ALLOW_EMPTY:${PN}-examples = "1"
FILES:${PN}-examples = "${bindir}"

NEATVNC_EXAMPLES = "draw png-server"

inherit meson pkgconfig

do_install:append () {
	if ${@bb.utils.contains('PACKAGECONFIG', 'examples', 'true', 'false', d)}; then
		install -d ${D}${bindir}
		for bin in ${NEATVNC_EXAMPLES}; do
			install -m 0755 ${B}/examples/$bin ${D}${bindir}
		done
	fi
}

BBCLASSEXTEND = "native"
