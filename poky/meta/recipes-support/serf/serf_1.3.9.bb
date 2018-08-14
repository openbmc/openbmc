SUMMARY = "High-Performance Asynchronous HTTP Client Library"
SRC_URI = "${APACHE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://norpath.patch \
           file://env.patch"

SRC_URI[md5sum] = "370a6340ff20366ab088012cd13f2b57"
SRC_URI[sha256sum] = "549c2d21c577a8a9c0450facb5cca809f26591f048e466552240947bdf7a87cc"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS = "python-scons-native openssl apr apr-util util-linux expat"

do_compile() {
	${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} PREFIX=${prefix} \
		CC="${CC}" \
		APR=`which apr-1-config` APU=`which apu-1-config` \
		CFLAGS="${CFLAGS}" LINKFLAGS="${LDFLAGS}" \
		OPENSSL="${STAGING_EXECPREFIXDIR}"
}

do_install() {
	${STAGING_BINDIR_NATIVE}/scons PREFIX=${D}${prefix} LIBDIR=${D}${libdir} install
}

BBCLASSEXTEND = "native"
