#
# Copyright (C) 2013 Wind River Systems, Inc.
#

SUMMARY = "Simple Protocol for Independent Computing Environments"
DESCRIPTION = "SPICE (the Simple Protocol for Independent Computing \
Environments) is a remote-display system built for virtual \
environments which allows users to view a computing 'desktop' \ 
environment - not only on its computer-server machine, but also from \
anywhere on the Internet and using a wide variety of machine \
architectures."

LICENSE = "BSD & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

PV = "0.14.2+git${SRCPV}"

SRCREV_spice = "7cbd70b931db76c69c89c2d9d5d704f67381a81b"
SRCREV_spice-common = "4fc4c2db36c7f07b906e9a326a9d3dc0ae6a2671"

SRCREV_FORMAT = "spice_spice-common"

SRC_URI = " \
    git://anongit.freedesktop.org/spice/spice;name=spice;branch=master \
    git://anongit.freedesktop.org/spice/spice-common;destsuffix=git/subprojects/spice-common;name=spice-common;branch=master \
    file://0001-Convert-pthread_t-to-be-numeric.patch \
    file://0001-Fix-compile-errors-on-Linux-32bit-system.patch \
"

S = "${WORKDIR}/git"

inherit autotools gettext python3native python3-dir pkgconfig

DEPENDS += "spice-protocol jpeg pixman alsa-lib glib-2.0 python3-pyparsing-native python3-six-native glib-2.0-native"
DEPENDS_append_class-nativesdk = "nativesdk-openssl"

export PYTHON="${STAGING_BINDIR_NATIVE}/python3-native/python3"

CFLAGS_append = " -Wno-error"

PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""
PACKAGECONFIG ?= "sasl"

PACKAGECONFIG[celt051] = "--enable-celt051,--disable-celt051,celt051"
PACKAGECONFIG[smartcard] = "--enable-smartcard,--disable-smartcard,libcacard,"
PACKAGECONFIG[sasl] = "--with-sasl,--without-sasl,cyrus-sasl,"
PACKAGECONFIG[client] = "--enable-client,--disable-client,,"
PACKAGECONFIG[gui] = "--enable-gui,--disable-gui,,"
PACKAGECONFIG[opus] = "--enable-opus,--disable-opus,libopus,"
PACKAGECONFIG[opengl] = "--enable-opengl,--disable-opengl,,"
PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,libxinerama,"

COMPATIBLE_HOST = '(x86_64|i.86).*-linux'

BBCLASSEXTEND = "native nativesdk"

EXTRA_OECONF_append_toolchain-clang = " --disable-werror"
