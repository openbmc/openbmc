SUMMARY = "An alternate posix capabilities library"
DESCRIPTION = "The libcap-ng library is intended to make programming \
with POSIX capabilities much easier than the traditional libcap library."
HOMEPAGE = "https://github.com/stevegrubb/libcap-ng"
SECTION = "base"
LICENSE = "GPL-2.0-or-later AND LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

SRC_URI = "git://github.com/stevegrubb/libcap-ng.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "9ae44402f4c405f08b9dd5ec070127d0d92afee4"

inherit lib_package autotools pkgconfig bash-completion

do_configure:prepend() {
    touch ${S}/NEWS
}

EXTRA_OECONF:append:class-target = " --with-capability_header=${STAGING_INCDIR}/linux/capability.h"
EXTRA_OECONF:append:class-nativesdk = " --with-capability_header=${STAGING_INCDIR}/linux/capability.h"

BBCLASSEXTEND = "native nativesdk"
