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

SRCREV = "a6668fff651726e5d4cfd52cbf8669540970452c"

SRC_URI = "gitsm://gitlab.freedesktop.org/spice/spice;branch=master;protocol=https;tag=v0.16.0 \
           file://0001-test-gst-Fix-compilation-error.patch \
           file://0002-test-display-base-fix.patch \
"

CVE_STATUS[CVE-2016-0749] = "fixed-version: patched since 0.13.2"
CVE_STATUS[CVE-2016-2150] = "fixed-version: patched since 0.13.2"
CVE_STATUS[CVE-2018-10893] = "fixed-version: patched already, caused by inaccurate CPE in the NVD database."

inherit meson pkgconfig python3native

DEPENDS = "spice-protocol glib-2.0 pixman openssl jpeg zlib python3-pyparsing-native"

do_configure:prepend() {
	echo ${PV} > ${S}/.tarball-version
}

EXTRA_OEMESON = "-Dtests=false"

PACKAGECONFIG:class-native = ""
PACKAGECONFIG:class-nativesdk = ""
PACKAGECONFIG ?= "lz4 sasl opus smartcard gstreamer"

PACKAGECONFIG[gstreamer] = "-Dgstreamer=1.0,-Dgstreamer=no,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[lz4] = "-Dlz4=true,-Dlz4=false,lz4"
PACKAGECONFIG[smartcard] = "-Dsmartcard=enabled,-Dsmartcard=disabled,libcacard,libcacard"
PACKAGECONFIG[sasl] = "-Dsasl=true,-Dsasl=false,cyrus-sasl,"
PACKAGECONFIG[opus] = "-Dopus=enabled,-Dopus=disabled,libopus,"

COMPATIBLE_HOST = '(x86_64|i.86|aarch64).*-linux'

BBCLASSEXTEND = "native nativesdk"
