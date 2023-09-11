SUMMARY = "Andri's Main Loop"
DESCRIPTION = "Andri's Main Loop"
HOMEPAGE = "https://github.com/any1/aml"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6f3cfaa39204b96e14b68b9d50d3e4e"

SRC_URI = "git://github.com/any1/aml;branch=master;protocol=https"

SRCREV = "b83f3576ce4187d9285f06e9066ef43a691464d4"

PV = "0.3.0+git${SRCPV}"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[examples] = "-Dexamples=true,-Dexamples=false"

PACKAGE_BEFORE_PN += "${PN}-examples"
ALLOW_EMPTY:${PN}-examples = "1"
FILES:${PN}-examples = "${bindir}"

inherit meson pkgconfig

AML_EXAMPLES = "ticker nested-ticker reader"

do_install:append () {
	if ${@bb.utils.contains('PACKAGECONFIG', 'examples', 'true', 'false', d)}; then
		install -d ${D}${bindir}
		for bin in ${AML_EXAMPLES}; do
			install -m 0755 ${B}/examples/$bin ${D}${bindir}
		done
	fi
}

BBCLASSEXTEND = "native"
