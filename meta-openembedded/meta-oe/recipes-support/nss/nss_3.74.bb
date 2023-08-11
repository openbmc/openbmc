SUMMARY = "Mozilla's SSL and TLS implementation"
DESCRIPTION = "Network Security Services (NSS) is a set of libraries \
designed to support cross-platform development of \
security-enabled client and server applications. \
Applications built with NSS can support SSL v2 and v3, \
TLS, PKCS 5, PKCS 7, PKCS 11, PKCS 12, S/MIME, X.509 \
v3 certificates, and other security standards."
HOMEPAGE = "http://www.mozilla.org/projects/security/pki/nss/"
SECTION = "libs"

DEPENDS = "sqlite3 nspr zlib nss-native"
DEPENDS:class-native = "sqlite3-native nspr-native zlib-native"

LICENSE = "(MPL-2.0 & MIT) | (MPL-2.0 & GPL-2.0-or-later & MIT) | (MPL-2.0 & LGPL-2.1-or-later & MIT)"

LIC_FILES_CHKSUM = "file://nss/COPYING;md5=3b1e88e1b9c0b5a4b2881d46cce06a18 \
                    file://nss/lib/freebl/mpi/doc/LICENSE;md5=491f158d09d948466afce85d6f1fe18f \
                    file://nss/lib/freebl/mpi/doc/LICENSE-MPL;md5=5d425c8f3157dbf212db2ec53d9e5132 \
                    file://nss/lib/freebl/verified/Hacl_Poly1305_256.c;beginline=1;endline=22;md5=d4096c1e4421ee56e9e0f441a8161f78"

VERSION_DIR = "${@d.getVar('BP').upper().replace('-', '_').replace('.', '_') + '_RTM'}"

SRC_URI = "http://ftp.mozilla.org/pub/security/nss/releases/${VERSION_DIR}/src/${BP}.tar.gz \
           file://nss.pc.in \
           file://0001-nss-fix-support-cross-compiling.patch \
           file://nss-no-rpath-for-cross-compiling.patch \
           file://nss-fix-incorrect-shebang-of-perl.patch \
           file://disable-Wvarargs-with-clang.patch \
           file://pqg.c-ULL_addend.patch \
           file://blank-cert9.db \
           file://blank-key4.db \
           file://system-pkcs11.txt \
           file://nss-fix-nsinstall-build.patch \
           file://0001-freebl-add-a-configure-option-to-disable-ARM-HW-cryp.patch \
           file://0001-Bug-1750624-Pin-validation-date-for-PayPalEE-test-ce.patch \
           "
SRC_URI[sha256sum] = "88928811f9f40f87d42e2eaccdf6e454562e51486067f2ddbe90aa47ea6cd056"

UPSTREAM_CHECK_URI = "https://developer.mozilla.org/en-US/docs/Mozilla/Projects/NSS/NSS_Releases"
UPSTREAM_CHECK_REGEX = "NSS_(?P<pver>.+)_release_notes"

inherit siteinfo

TD = "${S}/tentative-dist"
TDS = "${S}/tentative-dist-staging"

TARGET_CC_ARCH += "${LDFLAGS}"

CFLAGS:append:class-native = " -D_XOPEN_SOURCE "

do_configure:prepend:libc-musl () {
    sed -i -e '/-DHAVE_SYS_CDEFS_H/d' ${S}/nss/lib/dbm/config/config.mk
}

do_configure:prepend:powerpc64le:toolchain-clang () {
    sed -i -e 's/\-std=c99/\-std=gnu99/g' ${S}/nss/coreconf/command.mk
}

do_configure:prepend:powerpc64:toolchain-clang () {
    sed -i -e 's/\-std=c99/\-std=gnu99/g' ${S}/nss/coreconf/command.mk
}

do_compile:prepend:class-native() {
    export NSPR_INCLUDE_DIR=${STAGING_INCDIR_NATIVE}/nspr
    export NSPR_LIB_DIR=${STAGING_LIBDIR_NATIVE}
}

do_compile:prepend:class-nativesdk() {
    export LDFLAGS=""
}

do_compile:prepend:class-native() {
    # Need to set RPATH so that chrpath will do its job correctly
    RPATH="-Wl,-rpath-link,${STAGING_LIBDIR_NATIVE} -Wl,-rpath-link,${STAGING_BASE_LIBDIR_NATIVE} -Wl,-rpath,${STAGING_LIBDIR_NATIVE} -Wl,-rpath,${STAGING_BASE_LIBDIR_NATIVE}"
}

do_compile() {
    export NSPR_INCLUDE_DIR=${STAGING_INCDIR}/nspr

    export CROSS_COMPILE=1
    export NATIVE_CC="${BUILD_CC}"
    # Additional defines needed on Centos 7
    export NATIVE_FLAGS="${BUILD_CFLAGS} -DLINUX -Dlinux"
    export BUILD_OPT=1

    # POSIX.1-2001 states that the behaviour of getcwd() when passing a null
    # pointer as the buf argument, is unspecified.
    export NATIVE_FLAGS="${NATIVE_FLAGS} -DGETCWD_CANT_MALLOC"

    export FREEBL_NO_DEPEND=1
    export FREEBL_LOWHASH=1

    export LIBDIR=${libdir}
    export MOZILLA_CLIENT=1
    export NS_USE_GCC=1
    export NSS_USE_SYSTEM_SQLITE=1
    export NSS_ENABLE_ECC=1
    export NSS_ENABLE_WERROR=0

    ${@bb.utils.contains("TUNE_FEATURES", "crypto", "export NSS_USE_ARM_HW_CRYPTO=1", "", d)}

    export OS_RELEASE=3.4
    export OS_TARGET=Linux
    export OS_ARCH=Linux

    if [ "${TARGET_ARCH}" = "powerpc" ]; then
        OS_TEST=ppc
    elif [ "${TARGET_ARCH}" = "powerpc64" -o "${TARGET_ARCH}" = "powerpc64le" ]; then
        OS_TEST=ppc64
    elif [ "${TARGET_ARCH}" = "mips" -o "${TARGET_ARCH}" = "mipsel" -o "${TARGET_ARCH}" = "mips64" -o "${TARGET_ARCH}" = "mips64el" ]; then
        OS_TEST=mips
    elif [ "${TARGET_ARCH}" = "aarch64_be" ]; then
        OS_TEST="aarch64"
    else
        OS_TEST="${TARGET_ARCH}"
    fi

    if [ "${SITEINFO_BITS}" = "64" ]; then
        export USE_64=1
    elif [ "${TARGET_ARCH}" = "x86_64" -a "${SITEINFO_BITS}" = "32" ]; then
        export USE_X32=1
    fi

    export NSS_DISABLE_GTESTS=1
    # We can modify CC in the environment, but if we set it via an
    # argument to make, nsinstall, a host program, will also build with it!
    #
    # nss pretty much does its own thing with CFLAGS, so we put them into CC.
    # Optimization will get clobbered, but most of the stuff will survive.
    # The motivation for this is to point to the correct place for debug
    # source files and CFLAGS does that.  Nothing uses CCC.
    #
    export CC="${CC} ${CFLAGS}"
    make -C ./nss CCC="${CXX} -g" \
        OS_TEST=${OS_TEST} \
        RPATH="${RPATH}" \
        autobuild
}

do_compile[vardepsexclude] += "SITEINFO_BITS"

do_install:prepend:class-nativesdk() {
    export LDFLAGS=""
}

do_install() {
    export CROSS_COMPILE=1
    export NATIVE_CC="${BUILD_CC}"
    export BUILD_OPT=1

    export FREEBL_NO_DEPEND=1

    export LIBDIR=${libdir}
    export MOZILLA_CLIENT=1
    export NS_USE_GCC=1
    export NSS_USE_SYSTEM_SQLITE=1
    export NSS_ENABLE_ECC=1

    export OS_RELEASE=3.4
    export OS_TARGET=Linux
    export OS_ARCH=Linux

    if [ "${TARGET_ARCH}" = "powerpc" ]; then
        OS_TEST=ppc
    elif [ "${TARGET_ARCH}" = "powerpc64" -o "${TARGET_ARCH}" = "powerpc64le" ]; then
        OS_TEST=ppc64
    elif [ "${TARGET_ARCH}" = "mips" -o "${TARGET_ARCH}" = "mipsel" -o "${TARGET_ARCH}" = "mips64" -o "${TARGET_ARCH}" = "mips64el" ]; then
        OS_TEST=mips
    elif [ "${TARGET_ARCH}" = "aarch64_be" ]; then
        CPU_ARCH=aarch64
        OS_TEST="aarch64"
    else
        OS_TEST="${TARGET_ARCH}"
    fi
    if [ "${SITEINFO_BITS}" = "64" ]; then
        export USE_64=1
    elif [ "${TARGET_ARCH}" = "x86_64" -a "${SITEINFO_BITS}" = "32" ]; then
        export USE_X32=1
    fi

    export NSS_DISABLE_GTESTS=1

    make -C ./nss \
        CCC="${CXX}" \
        OS_TEST=${OS_TEST} \
        SOURCE_LIB_DIR="${TD}/${libdir}" \
        SOURCE_BIN_DIR="${TD}/${bindir}" \
        install

    install -d ${D}/${libdir}/
    for file in ${S}/dist/*.OBJ/lib/*.so; do
        echo "Installing `basename $file`..."
        cp $file  ${D}/${libdir}/
    done

    for shared_lib in ${TD}/${libdir}/*.so.*; do
        if [ -f $shared_lib ]; then
            cp $shared_lib ${D}/${libdir}
            ln -sf $(basename $shared_lib) ${D}/${libdir}/$(basename $shared_lib .1oe)
        fi
    done
    for shared_lib in ${TD}/${libdir}/*.so; do
        if [ -f $shared_lib -a ! -e ${D}/${libdir}/$shared_lib ]; then
            cp $shared_lib ${D}/${libdir}
        fi
    done

    install -d ${D}/${includedir}/nss3
    install -m 644 -t ${D}/${includedir}/nss3 dist/public/nss/*

    install -d ${D}/${bindir}
    for binary in ${TD}/${bindir}/*; do
        install -m 755 -t ${D}/${bindir} $binary
    done
}

do_install[vardepsexclude] += "SITEINFO_BITS"

do_install:append() {
    # Create empty .chk files for the NSS libraries at build time. They could
    # be regenerated at target's boot time.
    for file in libsoftokn3.chk libfreebl3.chk libnssdbm3.chk; do
        touch ${D}/${libdir}/$file
        chmod 755 ${D}/${libdir}/$file
    done

    install -d ${D}${libdir}/pkgconfig/
    sed 's/%NSS_VERSION%/${PV}/' ${WORKDIR}/nss.pc.in | sed 's/%NSPR_VERSION%/4.9.2/' > ${D}${libdir}/pkgconfig/nss.pc
    sed -i s:OEPREFIX:${prefix}:g ${D}${libdir}/pkgconfig/nss.pc
    sed -i s:OEEXECPREFIX:${exec_prefix}:g ${D}${libdir}/pkgconfig/nss.pc
    sed -i s:OELIBDIR:${libdir}:g ${D}${libdir}/pkgconfig/nss.pc
    sed -i s:OEINCDIR:${includedir}/nss3:g ${D}${libdir}/pkgconfig/nss.pc
}

do_install:append:class-target() {
    # It used to call certutil to create a blank certificate with empty password at
    # build time, but the checksum of key4.db changes every time when certutil is called.
    # It causes non-determinism issue, so provide databases with a blank certificate
    # which are originally from output of nss in qemux86-64 build. You can get these
    # databases by:
    # certutil -N -d sql:/database/path/ --empty-password
    install -d ${D}${sysconfdir}/pki/nssdb/
    install -m 0644 ${WORKDIR}/blank-cert9.db ${D}${sysconfdir}/pki/nssdb/cert9.db
    install -m 0644 ${WORKDIR}/blank-key4.db ${D}${sysconfdir}/pki/nssdb/key4.db
    install -m 0644 ${WORKDIR}/system-pkcs11.txt ${D}${sysconfdir}/pki/nssdb/pkcs11.txt
}

PACKAGE_WRITE_DEPS += "nss-native"

pkg_postinst:${PN} () {
    for I in $D${libdir}/lib*.chk; do
        DN=`dirname $I`
        BN=`basename $I .chk`
        FN=$DN/$BN.so
        shlibsign -i $FN
        if [ $? -ne 0 ]; then
            echo "shlibsign -i $FN failed"
        fi
    done
}

PACKAGES =+ "${PN}-smime"
FILES:${PN}-smime = "\
    ${bindir}/smime \
"

FILES:${PN} = "\
    ${sysconfdir} \
    ${bindir} \
    ${libdir}/lib*.chk \
    ${libdir}/lib*.so \
    "

FILES:${PN}-dev = "\
    ${libdir}/nss \
    ${libdir}/pkgconfig/* \
    ${includedir}/* \
    "

RDEPENDS:${PN}-smime = "perl"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT += "network_security_services"

CVE_STATUS_GROUPS += "CVE_STATUS_NSS"
CVE_STATUS_NSS[status] = "not-applicable-config: This only affect the legacy db (libnssdbm), only compiled with --enable-legacy-db"
CVE_STATUS_NSS = "CVE-2017-11695 CVE-2017-11696 CVE-2017-11697 CVE-2017-11698"

CVE_STATUS[CVE-2022-3479] = "not-applicable-config: vulnerability was introduced in 3.77 and fixed in 3.87"
