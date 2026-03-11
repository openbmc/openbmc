SUMMARY = "High-Performance Asynchronous HTTP Client Library"
DESCRIPTION = "The Apache Serf library is a C-based HTTP client library built upon the Apache \
Portable Runtime (APR) library. It multiplexes connections, running the \
read/write communication asynchronously. Memory copies and transformations are \
kept to a minimum to provide high performance operation."
HOMEPAGE = "http://serf.apache.org/"
SRC_URI = "${APACHE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://norpath.patch \
           file://env.patch \
           file://0002-SConstruct-Fix-path-quoting-for-.def-generator.patch \
           file://0003-gen_def.patch \
           file://SConstruct.stop.creating.directories.without.sandbox-install.prefix.patch \
           "

SRC_URI[sha256sum] = "be81ef08baa2516ecda76a77adf7def7bc3227eeb578b9a33b45f7b41dc064e6"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit scons

DEPENDS += " openssl apr apr-util util-linux expat"

EXTRA_OESCONS = " \
                  LIBDIR=${libdir} \
                  --install-sandbox=${D} \
                  CC="${CC}" \
                  CFLAGS="${CFLAGS}" \
                  LINKFLAGS="${LDFLAGS}" \
                  APR=`which apr-1-config` \
                  APU=`which apu-1-config` \
                  OPENSSL="${STAGING_EXECPREFIXDIR}" \
                  "

# scons creates non-reproducible archives
do_install:append() {
	rm ${D}/${libdir}/*.a
}

BBCLASSEXTEND = "native nativesdk"
