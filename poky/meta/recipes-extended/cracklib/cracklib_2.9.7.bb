SUMMARY = "Password strength checker library"
HOMEPAGE = "https://github.com/cracklib/cracklib"
DESCRIPTION = "${SUMMARY}"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

DEPENDS = "cracklib-native zlib"

EXTRA_OECONF = "--without-python --libdir=${base_libdir}"

SRC_URI = "git://github.com/cracklib/cracklib;protocol=https;branch=master \
           file://0001-packlib.c-support-dictionary-byte-order-dependent.patch \
           file://0002-craklib-fix-testnum-and-teststr-failed.patch"

SRCREV = "f83934cf3cced0c9600c7d81332f4169f122a2cf"
S = "${WORKDIR}/git/src"

inherit autotools gettext

# This is custom stuff from upstream's autogen.sh
do_configure:prepend() {
    mkdir -p ${S}/m4
    echo EXTRA_DIST = *.m4 > ${S}/m4/Makefile.am
    touch ${S}/ABOUT-NLS
}

do_install:append:class-target() {
	create-cracklib-dict -o ${D}${datadir}/cracklib/pw_dict ${D}${datadir}/cracklib/cracklib-small
}

BBCLASSEXTEND = "native nativesdk"

