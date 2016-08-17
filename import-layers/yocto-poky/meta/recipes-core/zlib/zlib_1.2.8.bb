SUMMARY = "Zlib Compression Library"
DESCRIPTION = "Zlib is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."
HOMEPAGE = "http://zlib.net/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://zlib.h;beginline=4;endline=23;md5=fde612df1e5933c428b73844a0c494fd"

SRC_URI = "http://www.zlib.net/${BPN}-${PV}.tar.xz \
           file://remove.ldconfig.call.patch \
           file://Makefile-runtests.patch \
           file://ldflags-tests.patch \
           file://run-ptest \
           "

SRC_URI[md5sum] = "28f1205d8dd2001f26fec1e8c2cebe37"
SRC_URI[sha256sum] = "831df043236df8e9a7667b9e3bb37e1fcb1220a0f163b6de2626774b9590d057"

RDEPENDS_${PN}-ptest += "make"

inherit ptest

do_configure (){
	./configure --prefix=${prefix} --shared --libdir=${libdir}
}

do_compile (){
	oe_runmake
}

do_compile_ptest() {
	oe_runmake static shared
}

do_install() {
	oe_runmake DESTDIR=${D} install
}

do_install_ptest() {
	install ${B}/Makefile   ${D}${PTEST_PATH}
	install ${B}/example    ${D}${PTEST_PATH}
	install ${B}/minigzip   ${D}${PTEST_PATH}
	install ${B}/examplesh  ${D}${PTEST_PATH}
	install ${B}/minigzipsh ${D}${PTEST_PATH}
}

# Move zlib shared libraries for target builds to $base_libdir so the library
# can be used in early boot before $prefix is mounted.
do_install_append_class-target() {
	if [ ${base_libdir} != ${libdir} ]
	then
		mkdir -p ${D}/${base_libdir}
		mv ${D}/${libdir}/libz.so.* ${D}/${base_libdir}
		libname=`readlink ${D}/${libdir}/libz.so`
		ln -sf ${@oe.path.relative("${libdir}", "${base_libdir}")}/$libname ${D}${libdir}/libz.so
	fi
}

BBCLASSEXTEND = "native nativesdk"
