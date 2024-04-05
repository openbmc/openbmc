# Copyright (C) 2015 Igor Santos <igor.santos@aker.com.br>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Universal Ctags"
DESCRIPTION = "Universal Ctags is a multilanguage reimplementation of the \
               Unix ctags utility. Ctags generates an index of source code \
               definitions which is used by numerous editors and utilities \
               to instantly locate the definitions."

HOMEPAGE = "https://ctags.io/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

inherit autotools-brokensep pkgconfig manpages

SRCREV = "38fd8e39b877a79666393b90e38400fd8d7d5d14"
SRC_URI = "git://github.com/universal-ctags/ctags;branch=master;protocol=https"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= " \
    readcmd \
    xml \
    json \
    yaml \
"
PACKAGECONFIG[readcmd] = "--enable-readcmd,--disable-readcmd"
PACKAGECONFIG[etags] = "--enable-etags,--disable-etags"
PACKAGECONFIG[xml] = "--enable-xml,--disable-xml,libxml2"
PACKAGECONFIG[json] = "--enable-json,--disable-json,jansson"
PACKAGECONFIG[seccomp] = "--enable-seccomp,--disable-seccomp,libseccomp"
PACKAGECONFIG[yaml] = "--enable-yaml,--disable-yaml,libyaml"
PACKAGECONFIG[manpages] = ",,python3-docutils-native"

BBCLASSEXTEND = "native"
