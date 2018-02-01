SUMMARY = "Multipurpose relay for bidirectional data transfer"
DESCRIPTION = "Socat is a relay for bidirectional data \
transfer between two independent data channels."
HOMEPAGE = "http://www.dest-unreach.org/socat/"

SECTION = "console/network"

DEPENDS = "openssl readline"

LICENSE = "GPL-2.0+-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://README;beginline=257;endline=287;md5=338c05eadd013872abb1d6e198e10a3f"


SRC_URI = "http://www.dest-unreach.org/socat/download/socat-${PV}.tar.bz2 \
           file://Makefile.in-fix-for-parallel-build.patch \
           file://0001-define-NETDB_INTERNAL-to-1-if-not-available.patch \
           file://0001-Access-c_ispeed-and-c_ospeed-via-APIs.patch \
"

SRC_URI[md5sum] = "607a24c15bd2cb54e9328bfbbd3a1ae9"
SRC_URI[sha256sum] = "e3561f808739383eb10fada1e5d4f26883f0311b34fd0af7837d0c95ef379251"

inherit autotools

EXTRA_AUTORECONF += "--exclude=autoheader"

EXTRA_OECONF += "ac_cv_have_z_modifier=yes \
        ac_cv_header_bsd_libutil_h=no \
"

PACKAGECONFIG_class-target ??= "tcp-wrappers"
PACKAGECONFIG ??= ""
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"

do_install_prepend () {
    mkdir -p ${D}${bindir}
    install -d ${D}${bindir} ${D}${mandir}/man1
}

BBCLASSEXTEND = "native nativesdk"
