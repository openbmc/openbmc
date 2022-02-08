# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Lightweight cross platform C++ GUID/UUID library"
HOMEPAGE = "https://github.com/graeme-hill/crossguid"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1373274bc8d8001edc54933919f36f68"
DEPENDS += "util-linux"

PV = "0.0+git${SRCPV}"

SRCREV = "b56957ac453575e91ca1b63a80c0077c2b0d011a"
SRC_URI = "git://github.com/graeme-hill/crossguid;protocol=https;branch=master"

S = "${WORKDIR}/git"

do_compile() {
	${CXX} -c guid.cpp -o guid.o ${CXXFLAGS} -std=c++11 -DGUID_LIBUUID
	${AR} rvs libcrossguid.a guid.o
}

do_install() {
	install -D -m 0644 ${B}/libcrossguid.a ${D}${libdir}/libcrossguid.a
	install -D -m 0644 ${S}/guid.h ${D}${includedir}/guid.h
}
