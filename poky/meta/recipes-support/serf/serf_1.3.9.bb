SUMMARY = "High-Performance Asynchronous HTTP Client Library"
DESCRIPTION = "The Apache Serf library is a C-based HTTP client library built upon the Apache \
Portable Runtime (APR) library. It multiplexes connections, running the \
read/write communication asynchronously. Memory copies and transformations are \
kept to a minimum to provide high performance operation."
SRC_URI = "${APACHE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://norpath.patch \
           file://env.patch \
           file://0001-Fix-syntax-of-a-print-in-the-scons-file-to-unbreak-b.patch \
           file://0002-SConstruct-Fix-path-quoting-for-.def-generator.patch \
           file://0003-gen_def.patch \
           file://0004-Follow-up-to-r1811083-fix-building-with-scons-3.0.0-.patch \
           file://SConstruct.stop.creating.directories.without.sandbox-install.prefix.patch \
           "

SRC_URI[md5sum] = "370a6340ff20366ab088012cd13f2b57"
SRC_URI[sha256sum] = "549c2d21c577a8a9c0450facb5cca809f26591f048e466552240947bdf7a87cc"

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
do_install_append() {
	rm ${D}/${libdir}/*.a
}

BBCLASSEXTEND = "native nativesdk"
