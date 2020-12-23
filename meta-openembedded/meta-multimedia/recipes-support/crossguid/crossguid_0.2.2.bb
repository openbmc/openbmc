# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Lightweight cross platform C++ GUID/UUID library"
HOMEPAGE = "https://github.com/graeme-hill/crossguid"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1373274bc8d8001edc54933919f36f68"
DEPENDS += "util-linux"

SRCREV = "5b45cdd9a56ca9da35ee0f8845cb4e2603d245dc"
SRC_URI = "git://github.com/graeme-hill/crossguid;protocol=https"

S = "${WORKDIR}/git"

inherit cmake

do_install() {
	install -D -m 0644 ${B}/libxg.a ${D}${libdir}/libxg.a
	install -D -m 0644 ${S}/Guid.hpp ${D}${includedir}/Guid.hpp
}
