SUMMARY = "Lightweight C library which eases the writing of UNIX daemons"
DESCRIPTION = "Lightweight daemon framework for OpenBSD. It provides \
facilities for logging and a signal handler to enable graceful shutdown, \
as well as file locking to ensure that only a single copy of a given daemon \
is running at a time."
SECTION = "libs"
AUTHOR = "Lennart Poettering <lennart@poettering.net>"
HOMEPAGE = "http://0pointer.de/lennart/projects/libdaemon/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://libdaemon/daemon.h;beginline=9;endline=21;md5=bd9fbe57cd96d1a5848a8ba12d9a6bf4"

SRC_URI = "http://0pointer.de/lennart/projects/libdaemon/libdaemon-${PV}.tar.gz \
           file://fix-includes.patch \
          "

SRC_URI[md5sum] = "509dc27107c21bcd9fbf2f95f5669563"
SRC_URI[sha256sum] = "fd23eb5f6f986dcc7e708307355ba3289abe03cc381fc47a80bca4a50aa6b834"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-lynx"
