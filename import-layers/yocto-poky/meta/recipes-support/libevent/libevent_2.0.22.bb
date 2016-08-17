SUMMARY = "An asynchronous event notification library"
HOMEPAGE = "http://libevent.org/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=50884&atid=461322"
SECTION = "libs"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=45c5316ff684bcfe2f9f86d8b1279559"

SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/levent/${BP}-stable.tar.gz \
    file://run-ptest \
"

SRC_URI[md5sum] = "c4c56f986aa985677ca1db89630a2e11"
SRC_URI[sha256sum] = "71c2c49f0adadacfdbe6332a372c38cf9c8b7895bb73dabeaa53cdcc1d4e1fa3"

UPSTREAM_CHECK_URI = "http://libevent.org/"

S = "${WORKDIR}/${BPN}-${PV}-stable"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"

inherit autotools

# Needed for Debian packaging
LEAD_SONAME = "libevent-2.0.so"

inherit ptest

DEPENDS = "zlib"

BBCLASSEXTEND = "native nativesdk"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/test
	for file in ${B}/test/.libs/regress ${B}/test/.libs/test*
	do
		install -m 0755 $file ${D}${PTEST_PATH}/test
	done
}
