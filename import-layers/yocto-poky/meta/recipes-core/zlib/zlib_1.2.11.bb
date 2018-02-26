SUMMARY = "Zlib Compression Library"
DESCRIPTION = "Zlib is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."
HOMEPAGE = "http://zlib.net/"
SECTION = "libs"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://zlib.h;beginline=6;endline=23;md5=5377232268e952e9ef63bc555f7aa6c0"

SRC_URI = "${SOURCEFORGE_MIRROR}/libpng/${BPN}/${PV}/${BPN}-${PV}.tar.xz \
           file://remove.ldconfig.call.patch \
           file://Makefile-runtests.patch \
           file://ldflags-tests.patch \
           file://run-ptest \
           "
UPSTREAM_CHECK_URI = "http://zlib.net/"

SRC_URI[md5sum] = "85adef240c5f370b308da8c938951a68"
SRC_URI[sha256sum] = "4ff941449631ace0d4d203e3483be9dbc9da454084111f97ea0a2114e19bf066"

CFLAGS += "-D_REENTRANT"

RDEPENDS_${PN}-ptest += "make"

inherit ptest

do_configure() {
	uname=GNU ./configure --prefix=${prefix} --shared --libdir=${libdir}
}

do_compile() {
	oe_runmake shared
}

do_compile_ptest() {
	oe_runmake test
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

	# Remove buildhost references...
	sed -i -e "s,--sysroot=${STAGING_DIR_TARGET},,g" \
		-e 's|${DEBUG_PREFIX_MAP}||g' \
	 ${D}${PTEST_PATH}/Makefile
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
