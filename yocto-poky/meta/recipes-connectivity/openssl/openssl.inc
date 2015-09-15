SUMMARY = "Secure Socket Layer"
DESCRIPTION = "Secure Socket Layer (SSL) binary and related cryptographic tools."
HOMEPAGE = "http://www.openssl.org/"
BUGTRACKER = "http://www.openssl.org/news/vulnerabilities.html"
SECTION = "libs/network"

# "openssl | SSLeay" dual license
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9a8f968107345e0b75aa8c2ecaa7ec8"

DEPENDS = "perl-native-runtime"
DEPENDS_append_class-target = " openssl-native"

SRC_URI = "http://www.openssl.org/source/openssl-${PV}.tar.gz \
          "
S = "${WORKDIR}/openssl-${PV}"

PACKAGECONFIG[perl] = ",,,"

AR_append = " r"
# Avoid binaries being marked as requiring an executable stack since it 
# doesn't(which causes and this causes issues with SELinux
CFLAG = "${@base_conditional('SITEINFO_ENDIANNESS', 'le', '-DL_ENDIAN', '-DB_ENDIAN', d)} \
	-DTERMIO ${CFLAGS} -Wall -Wa,--noexecstack"

# -02 does not work on mipsel: ssh hangs when it tries to read /dev/urandom
CFLAG_mtx-1 := "${@'${CFLAG}'.replace('-O2', '')}"
CFLAG_mtx-2 := "${@'${CFLAG}'.replace('-O2', '')}"

export DIRS = "crypto ssl apps"
export EX_LIBS = "-lgcc -ldl"
export AS = "${CC} -c"

inherit pkgconfig siteinfo multilib_header ptest

PACKAGES =+ "libcrypto libssl ${PN}-misc openssl-conf"
FILES_libcrypto = "${base_libdir}/libcrypto${SOLIBS}"
FILES_libssl = "${libdir}/libssl.so.*"
FILES_${PN} =+ " ${libdir}/ssl/*"
FILES_${PN}-misc = "${libdir}/ssl/misc ${bindir}/c_rehash"
RDEPENDS_${PN}-misc = "${@bb.utils.contains('PACKAGECONFIG', 'perl', 'perl', '', d)}"
FILES_${PN}-dev += "${base_libdir}/libcrypto${SOLIBSDEV}"

# Add the openssl.cnf file to the openssl-conf package.  Make the libcrypto
# package RRECOMMENDS on this package.  This will enable the configuration
# file to be installed for both the base openssl package and the libcrypto
# package since the base openssl package depends on the libcrypto package.
FILES_openssl-conf = "${libdir}/ssl/openssl.cnf"
CONFFILES_openssl-conf = "${libdir}/ssl/openssl.cnf"
RRECOMMENDS_libcrypto += "openssl-conf"
RDEPENDS_${PN}-ptest += "${PN}-misc make perl perl-module-filehandle bc"

# Remove this to enable SSLv3. SSLv3 is defaulted to disabled due to the POODLE
# vulnerability
EXTRA_OECONF = " -no-ssl3"

do_configure_prepend_darwin () {
	sed -i -e '/version-script=openssl\.ld/d' Configure
}

do_configure () {
	cd util
	perl perlpath.pl ${STAGING_BINDIR_NATIVE}
	cd ..
	ln -sf apps/openssl.pod crypto/crypto.pod ssl/ssl.pod doc/

	os=${HOST_OS}
	case $os in
	linux-uclibc |\
	linux-uclibceabi |\
	linux-gnueabi |\
	linux-uclibcspe |\
	linux-gnuspe |\
	linux-musl*)
		os=linux
		;;
		*)
		;;
	esac
	target="$os-${HOST_ARCH}"
	case $target in
	linux-arm)
		target=linux-armv4
		;;
	linux-armeb)
		target=linux-elf-armeb
		;;
	linux-aarch64*)
		target=linux-generic64
		;;
	linux-sh3)
		target=debian-sh3
		;;
	linux-sh4)
		target=debian-sh4
		;;
	linux-i486)
		target=debian-i386-i486
		;;
	linux-i586 | linux-viac3)
		target=debian-i386-i586
		;;
	linux-i686)
		target=debian-i386-i686/cmov
		;;
	linux-gnux32-x86_64)
		target=linux-x32
		;;
	linux-gnu64-x86_64)
		target=linux-x86_64
		;;
	linux-mips)
		target=debian-mips
		;;
	linux-mipsel)
		target=debian-mipsel
		;;
        linux-*-mips64)
               target=linux-mips
                ;;
	linux-microblaze*)
		target=linux-generic32
		;;
	linux-powerpc)
		target=linux-ppc
		;;
	linux-powerpc64)
		target=linux-ppc64
		;;
	linux-supersparc)
		target=linux-sparcv8
		;;
	linux-sparc)
		target=linux-sparcv8
		;;
	darwin-i386)
		target=darwin-i386-cc
		;;
	esac
	# inject machine-specific flags
	sed -i -e "s|^\(\"$target\",\s*\"[^:]\+\):\([^:]\+\)|\1:${CFLAG}|g" Configure
        useprefix=${prefix}
        if [ "x$useprefix" = "x" ]; then
                useprefix=/
        fi        
	perl ./Configure ${EXTRA_OECONF} shared --prefix=$useprefix --openssldir=${libdir}/ssl --libdir=`basename ${libdir}` $target
}

do_compile_prepend_class-target () {
    sed -i 's/\((OPENSSL=\)".*"/\1"openssl"/' Makefile
}

do_compile () {
	oe_runmake
}

do_compile_ptest () {
	oe_runmake buildtest
}

do_install () {
	# Create ${D}/${prefix} to fix parallel issues
	mkdir -p ${D}/${prefix}/

	oe_runmake INSTALL_PREFIX="${D}" MANDIR="${mandir}" install

	oe_libinstall -so libcrypto ${D}${libdir}
	oe_libinstall -so libssl ${D}${libdir}

	# Moving libcrypto to /lib
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mkdir -p ${D}/${base_libdir}/
		mv ${D}${libdir}/libcrypto* ${D}${base_libdir}/
		sed -i s#libdir=\$\{exec_prefix\}\/lib#libdir=${base_libdir}# ${D}/${libdir}/pkgconfig/libcrypto.pc
	fi

	install -d ${D}${includedir}
	cp --dereference -R include/openssl ${D}${includedir}

	oe_multilib_header openssl/opensslconf.h
	if [ "${@bb.utils.contains('PACKAGECONFIG', 'perl', 'perl', '', d)}" = "perl" ]; then
		install -m 0755 ${S}/tools/c_rehash ${D}${bindir}
		sed -i -e '1s,.*,#!${bindir}/env perl,' ${D}${bindir}/c_rehash
		sed -i -e '1s,.*,#!${bindir}/env perl,' ${D}${libdir}/ssl/misc/CA.pl
		sed -i -e '1s,.*,#!${bindir}/env perl,' ${D}${libdir}/ssl/misc/tsget
		# The c_rehash utility isn't installed by the normal installation process.
	else
		rm -f ${D}${bindir}/c_rehash
		rm -f ${D}${libdir}/ssl/misc/CA.pl ${D}${libdir}/ssl/misc/tsget
	fi
}

do_install_ptest () {
	cp -r Makefile test ${D}${PTEST_PATH}
	cp -r certs ${D}${PTEST_PATH}
	mkdir -p ${D}${PTEST_PATH}/apps
	ln -sf /usr/lib/ssl/misc/CA.sh  ${D}${PTEST_PATH}/apps
	ln -sf /usr/lib/ssl/openssl.cnf ${D}${PTEST_PATH}/apps
	ln -sf /usr/bin/openssl         ${D}${PTEST_PATH}/apps
	cp apps/server2.pem             ${D}${PTEST_PATH}/apps
	mkdir -p ${D}${PTEST_PATH}/util
	install util/opensslwrap.sh    ${D}${PTEST_PATH}/util
	install util/shlib_wrap.sh     ${D}${PTEST_PATH}/util
}

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/openssl \
	    OPENSSL_CONF=${libdir}/ssl/openssl.cnf \
	    SSL_CERT_DIR=${libdir}/ssl/certs \
	    SSL_CERT_FILE=${libdir}/ssl/cert.pem \
	    OPENSSL_ENGINES=${libdir}/ssl/engines
}

BBCLASSEXTEND = "native nativesdk"
