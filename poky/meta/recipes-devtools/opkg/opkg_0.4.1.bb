SUMMARY = "Open Package Manager"
SUMMARY_libopkg = "Open Package Manager library"
SECTION = "base"
HOMEPAGE = "http://code.google.com/p/opkg/"
BUGTRACKER = "http://code.google.com/p/opkg/issues/list"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/opkg.c;beginline=2;endline=21;md5=90435a519c6ea69ef22e4a88bcc52fa0"

DEPENDS = "libarchive"

PE = "1"

SRC_URI = "http://downloads.yoctoproject.org/releases/${BPN}/${BPN}-${PV}.tar.gz \
           file://opkg.conf \
           file://0001-opkg_conf-create-opkg.lock-in-run-instead-of-var-run.patch \
           file://run-ptest \
"

SRC_URI[md5sum] = "ba0c21305fc93b26e844981ef100dc85"
SRC_URI[sha256sum] = "45ac1e037d3877f635d883f8a555e172883a25d3eeb7986c75890fdd31250a43"

# This needs to be before ptest inherit, otherwise all ptest files end packaged
# in libopkg package if OPKGLIBDIR == libdir, because default
# PTEST_PATH ?= "${libdir}/${BPN}/ptest"
PACKAGES =+ "libopkg"

inherit autotools pkgconfig ptest

target_localstatedir := "${localstatedir}"
OPKGLIBDIR ??= "${target_localstatedir}/lib"

PACKAGECONFIG ??= "libsolv"

PACKAGECONFIG[gpg] = "--enable-gpg,--disable-gpg,\
    gnupg gpgme libgpg-error,\
    ${@ "gnupg" if ("native" in d.getVar("PN")) else "gnupg-gpg"}\
    "
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl"
PACKAGECONFIG[ssl-curl] = "--enable-ssl-curl,--disable-ssl-curl,curl openssl"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[sha256] = "--enable-sha256,--disable-sha256"
PACKAGECONFIG[libsolv] = "--with-libsolv,--without-libsolv,libsolv"

EXTRA_OECONF += " --disable-pathfinder"
EXTRA_OECONF_class-native = "--localstatedir=/${@os.path.relpath('${localstatedir}', '${STAGING_DIR_NATIVE}')} --sysconfdir=/${@os.path.relpath('${sysconfdir}', '${STAGING_DIR_NATIVE}')}"

# Release tarball has unused binaries on the tests folder, automatically created by automake.
# For now, delete them to avoid packaging errors (wrong architecture)
do_unpack_append () {
    bb.build.exec_func('remove_test_binaries', d)
}

remove_test_binaries () {
	rm ${WORKDIR}/opkg-${PV}/tests/libopkg_test*
}

do_install_append () {
	install -d ${D}${sysconfdir}/opkg
	install -m 0644 ${WORKDIR}/opkg.conf ${D}${sysconfdir}/opkg/opkg.conf
	echo "option lists_dir ${OPKGLIBDIR}/opkg/lists" >>${D}${sysconfdir}/opkg/opkg.conf

	# We need to create the lock directory
	install -d ${D}${OPKGLIBDIR}/opkg
}

do_install_ptest () {
	sed -i -e '/@echo $^/d' ${D}${PTEST_PATH}/tests/Makefile
	sed -i -e '/@PYTHONPATH=. $(PYTHON) $^/a\\t@if [ "$$?" != "0" ];then echo "FAIL:"$^;else echo "PASS:"$^;fi' ${D}${PTEST_PATH}/tests/Makefile
}

RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} opkg-arch-config libarchive"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}-ptest += "make binutils python3-core python3-compression"
RREPLACES_${PN} = "opkg-nogpg opkg-collateral"
RCONFLICTS_${PN} = "opkg-collateral"
RPROVIDES_${PN} = "opkg-collateral"

FILES_libopkg = "${libdir}/*.so.* ${OPKGLIBDIR}/opkg/"

BBCLASSEXTEND = "native nativesdk"

CONFFILES_${PN} = "${sysconfdir}/opkg/opkg.conf"
