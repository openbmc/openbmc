SUMMARY = "GNU Transport Layer Security Library"
HOMEPAGE = "http://www.gnu.org/software/gnutls/"
BUGTRACKER = "https://savannah.gnu.org/support/?group=gnutls"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-xx = "LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN}-openssl = "GPLv3+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=71391c8e0c1cfe68077e7fce3b586283 \
                    file://doc/COPYING;md5=c678957b0c8e964aa6c70fd77641a71e \
                    file://doc/COPYING.LESSER;md5=a6f89e2100d9b6cdffcea4f398e37343"

DEPENDS = "nettle gmp virtual/libiconv libunistring"
DEPENDS_append_libc-musl = " argp-standalone"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = "https://www.gnupg.org/ftp/gcrypt/gnutls/v${SHRT_VER}/gnutls-${PV}.tar.xz \
           file://arm_eabi.patch \
           file://posix-shell.patch \
"

SRC_URI[md5sum] = "9dcf0aa45d1a42e1b3ca5d39ec7c61a8"
SRC_URI[sha256sum] = "aa81944e5635de981171772857e72be231a7e0f559ae0292d2737de475383e83"

inherit autotools texinfo pkgconfig gettext lib_package gtk-doc

PACKAGECONFIG ??= "libidn"

# You must also have CONFIG_SECCOMP enabled in the kernel for
# seccomp to work.
PACKAGECONFIG[seccomp] = "ac_cv_libseccomp=yes,ac_cv_libseccomp=no,libseccomp"
PACKAGECONFIG[libidn] = "--with-idn,--without-idn,libidn2"
PACKAGECONFIG[libtasn1] = "--with-included-libtasn1=no,--with-included-libtasn1,libtasn1"
PACKAGECONFIG[p11-kit] = "--with-p11-kit,--without-p11-kit,p11-kit"
PACKAGECONFIG[tpm] = "--with-tpm,--without-tpm,trousers"

EXTRA_OECONF = " \
    --enable-doc \
    --disable-libdane \
    --disable-guile \
    --disable-rpath \
    --enable-local-libopts \
    --enable-openssl-compatibility \
    --with-libpthread-prefix=${STAGING_DIR_HOST}${prefix} \
    --with-default-trust-store-file=${sysconfdir}/ssl/certs/ca-certificates.crt \
"

# Otherwise the tools try and use HOSTTOOLS_DIR/bash as a shell.
export POSIX_SHELL="${base_bindir}/sh"

LDFLAGS_append_libc-musl = " -largp"

do_configure_prepend() {
	for dir in . lib; do
		rm -f ${dir}/aclocal.m4 ${dir}/m4/libtool.m4 ${dir}/m4/lt*.m4
	done
}

PACKAGES =+ "${PN}-openssl ${PN}-xx"

FILES_${PN}-dev += "${bindir}/gnutls-cli-debug"
FILES_${PN}-openssl = "${libdir}/libgnutls-openssl.so.*"
FILES_${PN}-xx = "${libdir}/libgnutlsxx.so.*"

BBCLASSEXTEND = "native nativesdk"
