SUMMARY = "GNU Transport Layer Security Library"
HOMEPAGE = "http://www.gnu.org/software/gnutls/"
BUGTRACKER = "https://savannah.gnu.org/support/?group=gnutls"
DEPENDS = "nettle"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-xx = "LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN}-openssl = "GPLv3+"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=a6f89e2100d9b6cdffcea4f398e37343"


SHRT_VER = "${@d.getVar('PV',1).split('.')[0]}.${@d.getVar('PV',1).split('.')[1]}"

SRC_URI = "ftp://ftp.gnutls.org/gcrypt/gnutls/v${SHRT_VER}/gnutls-${PV}.tar.xz"

inherit autotools texinfo binconfig pkgconfig gettext lib_package

EXTRA_OECONF="--disable-rpath \
              --with-included-libtasn1 \
              --enable-local-libopts \
              --with-libpthread-prefix=${STAGING_DIR_HOST}${prefix} \
              --disable-guile \
              --disable-crywrap \
              --without-p11-kit \
              "

do_configure_prepend() {
	for dir in . lib; do
		rm -f ${dir}/aclocal.m4 ${dir}/m4/libtool.m4 ${dir}/m4/lt*.m4
	done
}

PACKAGECONFIG ??= "zlib"
PACKAGECONFIG[tpm] = "--with-tpm, --without-tpm, trousers"
PACKAGECONFIG[zlib] = "--with-zlib, --without-zlib, zlib"

PACKAGES =+ "${PN}-openssl ${PN}-xx"

FILES_${PN}-dev += "${bindir}/gnutls-cli-debug"
FILES_${PN}-openssl = "${libdir}/libgnutls-openssl.so.*"
FILES_${PN}-xx = "${libdir}/libgnutlsxx.so.*"

LDFLAGS_append_libc-uclibc += " -pthread"

BBCLASSEXTEND = "native nativesdk"
