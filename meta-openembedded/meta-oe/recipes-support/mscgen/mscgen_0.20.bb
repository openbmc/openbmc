SUMMARY = "Mscgen is a small program that parses Message Sequence Chart descriptions and produces PNG, SVG, EPS or server side image maps (ismaps) as the output."
HOMEPAGE = "http://www.mcternan.me.uk/mscgen/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b1e6a340187c1cf716513439d07c1d79"

SRC_URI = "http://www.mcternan.me.uk/mscgen/software/${BPN}-src-${PV}.tar.gz"

SRC_URI[md5sum] = "65c90fb5150d7176b65b793f0faa7377"
SRC_URI[sha256sum] = "3c3481ae0599e1c2d30b7ed54ab45249127533ab2f20e768a0ae58d8551ddc23"

DEPENDS = "gd "

inherit autotools gettext

do_configure_prepend() {
	sed -i "s#AC_PATH_PROG(GDLIB_CONFIG,gdlib-config)#AC_PATH_PROG([GDLIB_CONFIG],[gdlib-config], ,[${STAGING_BINDIR_CROSS}])#" ${S}/configure.ac
}

BBCLASSEXTEND = "native nativesdk"
