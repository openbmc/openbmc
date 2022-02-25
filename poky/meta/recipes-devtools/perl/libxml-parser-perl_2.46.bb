SUMMARY = "XML::Parser - A perl module for parsing XML documents"
HOMEPAGE = "https://libexpat.github.io/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://Parser.pm;beginline=1;endline=7;md5=d12cc778c80fc4c518f0e5dee29fd5fb"

DEPENDS += "expat"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/XML-Parser-${PV}.tar.gz \
           file://ptest-perl/run-ptest \
           file://0001-CheckLib.pm-do-not-attempt-to-run-a-cross-executable.patch \
           "
SRC_URI[md5sum] = "80bb18a8e6240fcf7ec2f7b57601c170"
SRC_URI[sha256sum] = "d331332491c51cccfb4cb94ffc44f9cd73378e618498d4a37df9e043661c515d"

S = "${WORKDIR}/XML-Parser-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR} CC='${CC}' LD='${CCLD}' FULL_AR='${AR}'"

inherit cpan ptest-perl

# fix up sub MakeMaker project as arguments don't get propagated though
# see https://rt.cpan.org/Public/Bug/Display.html?id=28632
do_configure:append:class-target() {
	sed -E \
	    -e 's:-L${STAGING_LIBDIR}::g' -e 's:-I${STAGING_INCDIR}::g' \
	    -i Makefile Expat/Makefile
}

do_configure:append() {
	sed -e 's:--sysroot=.*\(\s\|$\):--sysroot=${STAGING_DIR_TARGET} :g' \
	    -i Makefile Expat/Makefile
	sed 's:^FULL_AR = .*:FULL_AR = ${AR}:g' -i Expat/Makefile
	# make sure these two do not build in parallel
	sed 's!^$(INST_DYNAMIC):!$(INST_DYNAMIC): $(BOOTSTRAP)!' -i Expat/Makefile
}

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_compile:class-native() {
	cpan_do_compile
}

do_install_ptest() {
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/xmlstats
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/xmlfilter
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/xmlcomments
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/canonical
	cp -r ${B}/samples ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}/samples
}

RDEPENDS:${PN} += "perl-module-carp perl-module-file-spec"
RDEPENDS:${PN}-ptest += "perl-module-filehandle perl-module-if perl-module-test perl-module-test-more"

BBCLASSEXTEND="native nativesdk"
