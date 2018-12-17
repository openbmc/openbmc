SUMMARY = "Utilities for manipulating filesystem extended attributes"
HOMEPAGE = "http://savannah.nongnu.org/projects/attr/"
SECTION = "libs"

DEPENDS = "virtual/libintl"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=2d0aa14b3fce4694e4f615e30186335f \
                    file://attr/attr.c;endline=17;md5=be0403261f0847e5f43ed5b08d19593c \
                    file://libattr/libattr.c;endline=17;md5=7970f77049f8fa1199fff62a7ab724fb"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/attr/${BP}.src.tar.gz \
           file://run-ptest \
"

require ea-acl.inc

# libdir should point to .la
do_install_append() {
	sed -i ${D}${libdir}/libattr.la -e \
	    s,^libdir=\'${base_libdir}\'$,libdir=\'${libdir}\',
}

inherit ptest update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "setfattr"
ALTERNATIVE_TARGET[setfattr] = "${bindir}/setfattr"

do_install_ptest() {
	tar -c --exclude=ext test/ | ( cd ${D}${PTEST_PATH} && tar -xf - )
	mkdir ${D}${PTEST_PATH}/include
	for i in builddefs buildmacros buildrules; \
	  do cp ${S}/include/$i ${D}${PTEST_PATH}/include/; \
	done
	sed -e 's|; @echo|; echo|' -i ${D}${PTEST_PATH}/test/Makefile
    
	# Remove any build host references
	sed -e "s:--sysroot=${STAGING_DIR_TARGET}::g" \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -i ${D}${PTEST_PATH}/include/builddefs
}

RDEPENDS_${PN}-ptest = "attr coreutils perl-module-filehandle perl-module-getopt-std perl-module-posix"

BBCLASSEXTEND = "native nativesdk"
