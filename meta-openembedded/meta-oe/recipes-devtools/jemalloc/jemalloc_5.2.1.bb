# Copyright (C) 2021 Mingli Yu <mingli.yu@windriver.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "General-purpose scalable concurrent malloc implementation"

DESCRIPTION = "jemalloc is a general purpose malloc(3) implementation that emphasizes \
fragmentation avoidance and scalable concurrency support."

HOMEPAGE = "https://github.com/jemalloc/jemalloc"
LICENSE = "BSD"

SECTION = "libs"

LIC_FILES_CHKSUM = "file://README;md5=6900e4a158982e4c4715bf16aa54fa10"

SRC_URI = "git://github.com/jemalloc/jemalloc.git"

SRCREV = "ea6b3e973b477b8061e0076bb257dbd7f3faa756"

S = "${WORKDIR}/git"

inherit autotools

EXTRA_AUTORECONF += "--exclude=autoheader"

EXTRA_OECONF:append:libc-musl = " --with-jemalloc-prefix=je_"
