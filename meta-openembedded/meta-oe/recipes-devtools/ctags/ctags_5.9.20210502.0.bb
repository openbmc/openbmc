# Copyright (C) 2015 Igor Santos <igor.santos@aker.com.br>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Universal Ctags"
DESCRIPTION = "Universal Ctags is a multilanguage reimplementation of the \
               Unix ctags utility. Ctags generates an index of source code \
               definitions which is used by numerous editors and utilities \
               to instantly locate the definitions."

HOMEPAGE = "https://ctags.io/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

inherit autotools-brokensep pkgconfig manpages

DEPENDS += "libxml2 jansson libyaml python3-docutils-native"
SRCREV = "6df08b82d4845d1b9420d9268f24d5db16ee4480"
SRC_URI = "git://github.com/universal-ctags/ctags"

S = "${WORKDIR}/git"

#do_install() {
#    install -Dm 755 ${B}/ctags ${D}${bindir}/ctags
#}
