# Copyright (C) 2017  - 2023 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "NIST Certified SCAP 1.2 toolkit"
HOME_URL = "https://www.open-scap.org/tools/openscap-base/"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
LICENSE = "LGPL-2.1-only"

DEPENDS = "dbus acl bzip2 pkgconfig gconf procps curl libxml2 libxslt libcap swig libpcre  xmlsec1"
DEPENDS:class-native = "pkgconfig-native swig-native curl-native libxml2-native libxslt-native libcap-native libpcre-native xmlsec1-native"

#March 18th, 2024
SRCREV = "6d008616978306ce5e68997dce554a1683064f8f"
SRC_URI = "git://github.com/OpenSCAP/openscap.git;branch=maint-1.3;protocol=https "

S = "${WORKDIR}/git"

inherit cmake pkgconfig python3native python3targetconfig perlnative systemd

PACKAGECONFIG ?= "python3 rpm perl gcrypt ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG[python3] = "-DENABLE_PYTHON3=ON, ,python3, python3"
PACKAGECONFIG[perl] = "-DENABLE_PERL=ON, ,perl, perl"
PACKAGECONFIG[rpm] = "-DENABLE_OSCAP_UTIL_AS_RPM=ON, ,rpm, rpm"
PACKAGECONFIG[gcrypt] = "-DWITH_CRYPTO=gcrypt, ,libgcrypt"
PACKAGECONFIG[nss3] = "-DWITH_CRYPTO=nss3, ,nss"
PACKAGECONFIG[selinux] = ", ,libselinux"
PACKAGECONFIG[remdediate_service] = "-DENABLE_OSCAP_REMEDIATE_SERVICE=ON,-DENABLE_OSCAP_REMEDIATE_SERVICE=NO,"

EXTRA_OECMAKE += "-DENABLE_PROBES_LINUX=ON -DENABLE_PROBES_UNIX=ON \
                  -DENABLE_PROBES_SOLARIS=OFF -DENABLE_PROBES_INDEPENDENT=ON \
                  -DENABLE_OSCAP_UTIL=ON -DENABLE_OSCAP_UTIL_SSH=ON \
                  -DENABLE_OSCAP_UTIL_DOCKER=OFF -DENABLE_OSCAP_UTIL_CHROOT=OFF \
                  -DENABLE_OSCAP_UTIL_PODMAN=OFF -DENABLE_OSCAP_UTIL_VM=OFF \
                  -DENABLE_PROBES_WINDOWS=OFF -DENABLE_VALGRIND=OFF \
                  -DENABLE_SCE=ON -DENABLE_MITRE=OFF -DENABLE_TESTS=OFF \
                  -DCMAKE_SKIP_INSTALL_RPATH=ON -DCMAKE_SKIP_RPATH=ON \
                  -DPREFERRED_PYTHON_PATH=${bindir}/python3 \
                  -DPYTHON3_PATH=${bindir}/python3 \
                  "

STAGING_OSCAP_DIR = "${TMPDIR}/work-shared/${MACHINE}/oscap-source"
STAGING_OSCAP_BUILDDIR = "${TMPDIR}/work-shared/openscap/oscap-build-artifacts"

do_configure:append:class-native () {
    sed -i 's:OSCAP_DEFAULT_CPE_PATH.*$:OSCAP_DEFAULT_CPE_PATH "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/cpe":' ${B}/config.h
    sed -i 's:OSCAP_DEFAULT_SCHEMA_PATH.*$:OSCAP_DEFAULT_SCHEMA_PATH "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/schemas":' ${B}/config.h
    sed -i 's:OSCAP_DEFAULT_XSLT_PATH.*$:OSCAP_DEFAULT_XSLT_PATH "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/xsl":' ${B}/config.h
}

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        if ${@bb.utils.contains('PACKAGECONFIG','remdediate_service','true','false',d)}; then
            install -D -m 0644 ${B}/oscap-remediate.service ${D}${systemd_system_unitdir}/oscap-remediate.service
        fi
    fi
}

do_install:class-native[cleandirs] += " ${STAGING_OSCAP_BUILDDIR}"
do_install:append:class-native () {
    oscapdir=${STAGING_OSCAP_BUILDDIR}/${datadir_native}
    install -d $oscapdir
    cp -a ${D}/${STAGING_DATADIR_NATIVE}/openscap $oscapdir
}


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('PACKAGECONFIG','remdediate_service', 'oscap-remediate.service', '',d)}"
SYSTEMD_AUTO_ENABLE = "disable"


FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"


RDEPENDS:${PN} = "libxml2 python3-core libgcc bash"
RDEPENDS:${PN}-class-target = "libxml2 python3-core libgcc bash os-release"
BBCLASSEXTEND = "native"
