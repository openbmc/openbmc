# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "API to access the kernel tracefs directory"
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/libtrace/libtracefs.git/"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
                    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"
SECTION = "libs"

SRCREV = "dd148189b74da3e2f45c7e536319fec97cb71213"
SRC_URI = "git://git.kernel.org/pub/scm/libs/libtrace/libtraceevent.git;branch=${BPN};protocol=https \
           file://meson.patch"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = "-Ddocs=false"

PACKAGES += "${PN}-plugins"

FILES:${PN}-plugins += "${libdir}/traceevent/plugins"
