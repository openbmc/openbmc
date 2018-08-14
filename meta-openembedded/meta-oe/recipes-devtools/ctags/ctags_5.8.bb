# Copyright (C) 2015 Igor Santos <igor.santos@aker.com.br>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Exuberant Ctags"
DESCRIPTION = "Exuberant Ctags is a multilanguage reimplementation of the \
               Unix ctags utility. Ctags generates an index of source code \
               definitions which is used by numerous editors and utilities \
               to instantly locate the definitions."

HOMEPAGE = "http://ctags.sourceforge.net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

inherit autotools-brokensep

SRC_URI = "http://prdownloads.sourceforge.net/${BPN}/${BP}.tar.gz"

SRC_URI[md5sum] = "c00f82ecdcc357434731913e5b48630d"
SRC_URI[sha256sum] = "0e44b45dcabe969e0bbbb11e30c246f81abe5d32012db37395eb57d66e9e99c7"

do_install() {
    install -Dm 755 ${B}/ctags ${D}${bindir}/ctags
    install -Dm 644 ${B}/ctags.1 ${D}${mandir}/man1/ctags.1
}
