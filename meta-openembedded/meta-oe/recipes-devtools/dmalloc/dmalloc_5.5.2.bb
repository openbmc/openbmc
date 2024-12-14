# Copyright (C) 2016 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Debug Malloc Library"

DESCRIPTION = "The debug memory allocation or dmalloc library has been \
designed as a drop in replacement for the system's malloc, realloc, \
calloc, free and other memory management routines while providing \
powerful debugging facilities configurable at runtime. These facilities \
include such things as memory-leak tracking, fence-post write detection, \
file/line number reporting, and general logging of statistics."

HOMEPAGE = "http://dmalloc.com/"
LICENSE = "CC-BY-SA-3.0"

LIC_FILES_CHKSUM = "file://dmalloc.c;beginline=4;endline=17;md5=83d13664f87f1f1a3b6b2b6f6eba85aa"

SECTION = "libs"

SRC_URI = "http://dmalloc.com/releases/dmalloc-${PV}.tgz \
           file://02-Makefile.in.patch \
           file://03-threads.patch \
           file://13-fix-ldflags-in-makefile.patch \
           file://configure-pagesize-HACK.patch \
           file://100-use-xtools.patch  \
           file://130-mips.patch \
           file://150-use_DESTDIR.patch \
           file://0001-undefined-strdup-macro.patch \
"

SRC_URI[sha256sum] = "d3be5c6eec24950cb3bd67dbfbcdf036f1278fae5fd78655ef8cdf9e911e428a"

ARM_INSTRUCTION_SET = "arm"

inherit autotools

EXTRA_AUTORECONF += "--include=acinclude --exclude=autoheader"
EXTRA_OECONF += "--enable-threads --enable-cxx --enable-shlib"
