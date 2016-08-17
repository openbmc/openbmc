SUMMARY = "XML::Parser - A perl module for parsing XML documents"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=2;endline=6;md5=c8767d7516229f07b26e42d1cf8b51f1"

DEPENDS += "expat expat-native"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/XML-Parser-${PV}.tar.gz"
SRC_URI[md5sum] = "af4813fe3952362451201ced6fbce379"
SRC_URI[sha256sum] = "1ae9d07ee9c35326b3d9aad56eae71a6730a73a116b9fe9e8a4758b7cc033216"

S = "${WORKDIR}/XML-Parser-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR} CC='${CC}' LD='${CCLD}' FULL_AR='${AR}'"

inherit cpan

# fix up sub MakeMaker project as arguments don't get propagated though
# see https://rt.cpan.org/Public/Bug/Display.html?id=28632
do_configure_append() {
	sed 's:--sysroot=.*\(\s\|$\):--sysroot=${STAGING_DIR_TARGET} :g' -i Makefile Expat/Makefile
	sed 's:^FULL_AR = .*:FULL_AR = ${AR}:g' -i Expat/Makefile
}

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_compile_class-native() {
	cpan_do_compile
}

BBCLASSEXTEND="native"

