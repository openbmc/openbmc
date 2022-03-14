# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "rpcsvc protocol definitions from glibc"

DESCRIPTION = "This package contains rpcsvc proto.x files from glibc, which are\
missing in libtirpc. Additional it contains rpcgen, which is needed\
to create header files and sources from protocol files.\
This package is only needed, if glibc is installed without the\
deprecated sunrpc functionality and libtirpc should replace it."

HOMEPAGE = "https://github.com/thkukuk/rpcsvc-proto"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=0daaf958d5531ab86169ec6e275e1517"
SECTION = "libs"
DEPENDS += "rpcsvc-proto-native"

PV = "1.4.3"

SRCREV = "71e0a12c04d130a78674ac6309eefffa6ecee612"

SRC_URI = "git://github.com/thkukuk/${BPN};branch=master;protocol=https \
           file://0001-Use-cross-compiled-rpcgen.patch \
          "

S = "${WORKDIR}/git"

inherit autotools gettext

EXTRA_OEMAKE:class-native = " -C rpcgen"

do_configure:prepend() {
	touch ${S}/ABOUT-NLS
}

do_install:append() {
	# They come from quota recipe
	rm -rf ${D}${includedir}/rpcsvc/rquota.[hx]
}

BBCLASSEXTEND += "native nativesdk"
