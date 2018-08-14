# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "SCAP content for various platforms"
HOME_URL = "https://www.open-scap.org/security-policies/scap-security-guide/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=236e81befc8154d18c93c848185d7e52"
LICENSE = "LGPL-2.1"

DEPENDS = "openscap-native"

SRCREV = "423d9f40021a03abd018bef7818a3a9fe91a083c"
SRC_URI = "git://github.com/akuster/scap-security-guide.git;branch=oe;"

inherit cmake

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

STAGING_OSCAP_BUILDDIR = "${TMPDIR}/work-shared/openscap/oscap-build-artifacts"

EXTRA_OECMAKE += "-DSSG_PRODUCT_CHROMIUM:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_DEBIAN8:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_FEDORA:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_FIREFOX:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_JBOSS_EAP5:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_JBOSS_FUSE6:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_JRE:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_OPENSUSE:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_OSP7:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_RHEL5:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_RHEL6:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_RHEL7:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_RHEV3:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_SUSE11:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_SUSE12:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_UBUNTU1404:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_UBUNTU1604:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_WRLINUX:BOOL=OFF"
EXTRA_OECMAKE += "-DSSG_PRODUCT_WEBMIN:BOOL=OFF"

do_configure_prepend () {
	sed -i -e 's:NAMES\ sed:NAMES\ ${HOSTTOOLS_DIR}/sed:g'   ${S}/CMakeLists.txt
        sed -i 's:/usr/share/openscap/:${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/:g' ${S}/cmake/SSGCommon.cmake
}

do_compile () {
	cd ${B}
	make openembedded 
}

do_install () {
	cd ${B}
	make DESTDIR=${D} install
}
FILES_${PN} += "${datadir}/xml"
RDEPNEDS_${PN} = "openscap"
