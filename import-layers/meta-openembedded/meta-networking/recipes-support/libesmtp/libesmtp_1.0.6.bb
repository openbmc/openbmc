SUMMARY = "SMTP client library"
DESCRIPTION = "LibESMTP is a library to manage posting \
(or submission of) electronic mail using SMTP to a \
preconfigured Mail Transport Agent (MTA) such as Exim or PostFix."
HOMEPAGE = "http://www.stafford.uklinux.net/libesmtp/"
LICENSE = "LGPLv2+"
SECTION = "libs"

DEPENDS = "openssl"

SRC_URI = "http://www.stafford.uklinux.net/libesmtp/libesmtp-${PV}.tar.bz2 \
           file://include-topdir.patch"
SRC_URI[md5sum] = "bf3915e627fd8f35524a8fdfeed979c8"
SRC_URI[sha256sum] = "d0a61a5c52d99fa7ce7d00ed0a07e341dbda67101dbed1ab0cdae3f37db4eb0b"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

inherit autotools binconfig

EXTRA_OECONF = " \
    --with-auth-plugin-dir=${libdir}/esmtp-plugins \
    --enable-pthreads                              \
    --enable-debug                                 \
    --enable-etrn                                  \
    --disable-isoc                                 \
    --disable-more-warnings                        \
    --disable-static                               \
"

FILES_${PN} = "${libdir}/lib*${SOLIBS} \
               ${libdir}/esmtp-plugins/*${SOLIBSDEV}"

FILES_${PN}-dev  += "${libdir}/esmtp-plugins/*.la"
FILES_${PN}-static += "${libdir}/esmtp-plugins/*.a"
FILES_${PN}-dbg += "${libdir}/esmtp-plugins/.debug/"
