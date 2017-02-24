SUMMARY = "High-Performance Asynchronous HTTP Client Library"
SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/s/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://norpath.patch \
           file://env.patch"

SRC_URI[md5sum] = "713beaf05d7f3329de121e218e2fcb93"
SRC_URI[sha256sum] = "77134cd5010664ca023585bce50978bd4005906ed280ff889f591f86df7c59e4"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/s/serf/"

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
