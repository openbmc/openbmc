SUMMARY = "Zlib Compression Library"
DESCRIPTION = "Zlib is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."
HOMEPAGE = "http://zlib.net/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://zlib.h;beginline=6;endline=23;md5=5377232268e952e9ef63bc555f7aa6c0"

SRC_URI = "https://zlib.net/${BP}.tar.xz \
           file://cc.patch \
           file://ldflags-tests.patch \
           file://0001-configure-Pass-LDFLAGS-to-link-tests.patch \
           file://run-ptest \
           file://0001-Correct-incorrect-inputs-provided-to-the-CRC-functio.patch \
           "
UPSTREAM_CHECK_URI = "http://zlib.net/"

SRC_URI[sha256sum] = "7db46b8d7726232a621befaab4a1c870f00a90805511c0e0090441dac57def18"

CFLAGS += "-D_REENTRANT"

RDEPENDS:${PN}-ptest += "make"

inherit ptest

do_configure() {
	LDCONFIG=true ./configure --prefix=${prefix} --shared --libdir=${libdir} --uname=GNU
}

do_compile() {
	oe_runmake shared
}

do_install() {
	oe_runmake DESTDIR=${D} install
}

do_install_ptest() {
	install ${B}/examplesh ${D}${PTEST_PATH}
}

# Move zlib shared libraries for target builds to $base_libdir so the library
# can be used in early boot before $prefix is mounted.
do_install:append:class-target() {
	if [ ${base_libdir} != ${libdir} ]
	then
		mkdir -p ${D}/${base_libdir}
		mv ${D}/${libdir}/libz.so.* ${D}/${base_libdir}
		libname=`readlink ${D}/${libdir}/libz.so`
		ln -sf ${@oe.path.relative("${libdir}", "${base_libdir}")}/$libname ${D}${libdir}/libz.so
	fi
}

BBCLASSEXTEND = "native nativesdk"
