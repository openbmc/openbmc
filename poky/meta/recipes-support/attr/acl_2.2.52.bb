SUMMARY = "Utilities for managing POSIX Access Control Lists"
HOMEPAGE = "http://savannah.nongnu.org/projects/acl/"
SECTION = "libs"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=c781d70ed2b4d48995b790403217a249 \
                    file://doc/COPYING.LGPL;md5=9e9a206917f8af112da634ce3ab41764"

DEPENDS = "attr"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/acl/${BP}.src.tar.gz \
           file://configure.ac;subdir=${BP} \
           file://run-ptest \
           file://acl-fix-the-order-of-expected-output-of-getfacl.patch \
           file://test-fix-insufficient-quoting-of.patch \
           file://test-fixups-on-SELinux-machines-for-root-testcases.patch \
           file://test-fix-directory-permissions.patch \
           file://Makefile-libacl-should-depend-on-include.patch \
"

SRC_URI[md5sum] = "a61415312426e9c2212bd7dc7929abda"
SRC_URI[sha256sum] = "179074bb0580c06c4b4137be4c5a92a701583277967acdb5546043c7874e0d23"

require ea-acl.inc

# avoid RPATH hardcode to staging dir
do_configure_append() {
	sed -i ${S}/config.status -e s,^\\\(hardcode_into_libs=\\\).*$,\\1\'no\',
	${S}/config.status
}

# libdir should point to .la
do_install_append() {
	sed -i ${D}${libdir}/libacl.la -e \
	    s,^libdir=\'${base_libdir}\'$,libdir=\'${libdir}\',
}

inherit ptest

do_install_ptest() {
	tar -c --exclude=nfs test/ | ( cd ${D}${PTEST_PATH} && tar -xf - )
	mkdir ${D}${PTEST_PATH}/include
	cp ${S}/include/builddefs ${S}/include/buildmacros ${S}/include/buildrules ${D}${PTEST_PATH}/include/
	# Remove any build host references
	sed -e "s:--sysroot=${STAGING_DIR_TARGET}::g" \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -i ${D}${PTEST_PATH}/include/builddefs
}

RDEPENDS_${PN}-ptest = "acl bash coreutils perl perl-module-filehandle perl-module-getopt-std perl-module-posix shadow"

BBCLASSEXTEND = "native nativesdk"
