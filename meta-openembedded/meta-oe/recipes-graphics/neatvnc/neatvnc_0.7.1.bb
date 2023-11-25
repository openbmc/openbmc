SUMMARY = "A liberally licensed VNC server library"
DESCRIPTION = "This is a liberally licensed VNC server library that's intended to be fast and neat."
HOMEPAGE = "https://github.com/any1/neatvnc"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=94fc374e7174f41e3afe0f027ee59ff7"

SRC_URI = "git://github.com/any1/neatvnc;branch=v0.7;protocol=https"

SRCREV = "b5b330b22c6a0fdeb718a9e7ee0732cc406863fa"

S = "${WORKDIR}/git"

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
