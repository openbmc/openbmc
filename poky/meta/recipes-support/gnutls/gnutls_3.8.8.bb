SUMMARY = "GNU Transport Layer Security Library"
DESCRIPTION = "a secure communications library implementing the SSL, \
TLS and DTLS protocols and technologies around them."
HOMEPAGE = "https://gnutls.org/"
BUGTRACKER = "https://savannah.gnu.org/support/?group=gnutls"

LICENSE = "GPL-3.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LICENSE:${PN}-xx = "LGPL-2.1-or-later"
LICENSE:${PN}-bin = "GPL-3.0-or-later"
LICENSE:${PN}-openssl = "GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=71391c8e0c1cfe68077e7fce3b586283 \
                    file://doc/COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://doc/COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "nettle gmp virtual/libiconv libunistring"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = "https://www.gnupg.org/ftp/gcrypt/gnutls/v${SHRT_VER}/gnutls-${PV}.tar.xz \
           file://arm_eabi.patch \
           file://0001-Creating-.hmac-file-should-be-excuted-in-target-envi.patch \
           file://run-ptest \
           file://Add-ptest-support.patch \
           "

SRC_URI[sha256sum] = "ac4f020e583880b51380ed226e59033244bc536cad2623f2e26f5afa2939d8fb"

inherit autotools texinfo pkgconfig gettext lib_package gtk-doc ptest

PACKAGECONFIG ??= "libidn libtasn1 ${@bb.utils.filter('DISTRO_FEATURES', 'seccomp', d)}"

# You must also have CONFIG_SECCOMP enabled in the kernel for
# seccomp to work.
PACKAGECONFIG[seccomp] = "--with-libseccomp-prefix=${STAGING_EXECPREFIXDIR},ac_cv_libseccomp=no,libseccomp"
PACKAGECONFIG[libidn] = "--with-idn,--without-idn,libidn2"
PACKAGECONFIG[libtasn1] = "--without-included-libtasn1,--with-included-libtasn1,libtasn1"
PACKAGECONFIG[p11-kit] = "--with-p11-kit,--without-p11-kit,p11-kit"
PACKAGECONFIG[tpm] = "--with-tpm,--without-tpm,trousers"
PACKAGECONFIG[fips] = "--enable-fips140-mode --with-libdl-prefix=${STAGING_BASELIBDIR}"
PACKAGECONFIG[dane] = "--enable-libdane,--disable-libdane,unbound"
# Certificate compression
PACKAGECONFIG[brotli] = "--with-brotli,--without-brotli,brotli"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"

EXTRA_OECONF = " \
    --enable-doc \
    --disable-rpath \
    --enable-openssl-compatibility \
    --with-libpthread-prefix=${STAGING_DIR_HOST}${prefix} \
    --with-librt-prefix=${STAGING_DIR_HOST}${prefix} \
    --with-default-trust-store-file=${sysconfdir}/ssl/certs/ca-certificates.crt \
"

# Otherwise the tools try and use HOSTTOOLS_DIR/bash as a shell.
export POSIX_SHELL = "${base_bindir}/sh"

do_configure:prepend() {
	for dir in . lib; do
		rm -f ${dir}/aclocal.m4 ${dir}/m4/libtool.m4 ${dir}/m4/lt*.m4
	done
}

do_compile_ptest() {
    oe_runmake -C tests buildtest-TESTS
}

do_install:append:class-target() {
        if ${@bb.utils.contains('PACKAGECONFIG', 'fips', 'true', 'false', d)}; then
          install -d ${D}${bindir}/bin
          install -m 0755 ${B}/lib/.libs/fipshmac ${D}/${bindir}/
        fi
}

PACKAGES =+ "${PN}-dane ${PN}-openssl ${PN}-xx ${PN}-fips"

FILES:${PN}-dev += "${bindir}/gnutls-cli-debug"

FILES:${PN}-dane = "${libdir}/libgnutls-dane.so.*"
FILES:${PN}-openssl = "${libdir}/libgnutls-openssl.so.*"
FILES:${PN}-xx = "${libdir}/libgnutlsxx.so.*"
FILES:${PN}-fips = "${bindir}/fipshmac"

RDEPENDS:${PN}-ptest += "python3"

BBCLASSEXTEND = "native nativesdk"

pkg_postinst_ontarget:${PN}-fips () {
    if test -x ${bindir}/fipshmac
    then
        mkdir ${sysconfdir}/gnutls
        touch ${sysconfdir}/gnutls/config
        ${bindir}/fipshmac ${libdir}/libgnutls.so.30.*.* > ${libdir}/.libgnutls.so.30.hmac
        ${bindir}/fipshmac ${libdir}/libnettle.so.8.* > ${libdir}/.libnettle.so.8.hmac
        ${bindir}/fipshmac ${libdir}/libgmp.so.10.*.* > ${libdir}/.libgmp.so.10.hmac
        ${bindir}/fipshmac ${libdir}/libhogweed.so.6.* > ${libdir}/.libhogweed.so.6.hmac
    fi
}
