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

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

PV = "0.15.2"

SRCREV = "0c2c1413a8b387ea597a95b6c867470a7c56c8ab"

SRC_URI = "gitsm://gitlab.freedesktop.org/spice/spice;branch=master;protocol=https"

S = "${WORKDIR}/git"

CVE_STATUS[CVE-2018-10893] = "fixed-version: patched already, caused by inaccurate CPE in the NVD database."

inherit meson gettext python3native python3-dir pkgconfig

DEPENDS = "spice-protocol jpeg pixman alsa-lib glib-2.0 gdk-pixbuf lz4 orc python3-pyparsing-native python3-six-native glib-2.0-native zlib"
DEPENDS:append:class-nativesdk = " nativesdk-openssl"

export PYTHON="${STAGING_BINDIR_NATIVE}/python3-native/python3"

do_configure:prepend() {
	echo ${PV} > ${S}/.tarball-version
}

PACKAGECONFIG:class-native = ""
PACKAGECONFIG:class-nativesdk = ""
PACKAGECONFIG ?= "sasl opus smartcard gstreamer"

PACKAGECONFIG[gstreamer] = "-Dgstreamer=1.0,-Dgstreamer=no,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[smartcard] = "-Dsmartcard=enabled,-Dsmartcard=disabled,libcacard,libcacard"
PACKAGECONFIG[sasl] = "-Dsasl=true,-Dsasl=false,cyrus-sasl,"
PACKAGECONFIG[opus] = "-Dopus=enabled,-Dopus=disabled,libopus,"

COMPATIBLE_HOST = '(x86_64|i.86|aarch64).*-linux'

BBCLASSEXTEND = "native nativesdk"
