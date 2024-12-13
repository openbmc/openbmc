require automake.inc
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS:class-native = "autoconf-native"

NAMEVER = "${@oe.utils.trim_version("${PV}", 2)}"

RDEPENDS:${PN} += "\
    autoconf \
    perl \
    perl-module-bytes \
    perl-module-data-dumper \
    perl-module-strict \
    perl-module-text-parsewords \
    perl-module-thread-queue \
    perl-module-threads \
    perl-module-vars "

RDEPENDS:${PN}:class-native = "autoconf-native hostperl-runtime-native"

SRC_URI += "\
           file://0001-automake-Add-default-libtool_tag-to-cppasm.patch \
           file://0002-automake-Update-for-python.m4-to-respect-libdir.patch \
           file://0003-build-fix-race-in-parallel-builds.patch \
           file://0004-Add-a-new-distro-feature-ptest.patch \
           file://0005-Set-relative-to-top_builddir-path-in-Makefile-to-acc.patch \
           file://0006-automake-Remove-delays-in-configure-scripts-using-au.patch \
           "

SRC_URI[sha256sum] = "397767d4db3018dd4440825b60c64258b636eaf6bf99ac8b0897f06c89310acd"

do_install:append () {
    install -d ${D}${datadir}
    sed -i -e 's@^#!.*perl$@#!/usr/bin/env perl@' ${D}/${bindir}/*
}

BBCLASSEXTEND = "native nativesdk"
