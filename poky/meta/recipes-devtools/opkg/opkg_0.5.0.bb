SUMMARY = "Open Package Manager"
SUMMARY:libopkg = "Open Package Manager library"
SECTION = "base"
HOMEPAGE = "http://code.google.com/p/opkg/"
DESCRIPTION = "Opkg is a lightweight package management system based on Ipkg."
BUGTRACKER = "http://code.google.com/p/opkg/issues/list"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/opkg.c;beginline=4;endline=18;md5=d6200b0f2b41dee278aa5fad333eecae"

DEPENDS = "libarchive"

PE = "1"

SRC_URI = "http://downloads.yoctoproject.org/releases/${BPN}/${BPN}-${PV}.tar.gz \
           file://opkg.conf \
           file://0001-opkg_conf-create-opkg.lock-in-run-instead-of-var-run.patch \
           file://run-ptest \
"

SRC_URI[sha256sum] = "559c3e1b893abaa1dd473ce3a9a5f7dd3f60ceb6cd14caaef76ddf0f7721ad1c"

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
PACKAGECONFIG[sha256] = "--enable-sha256,--disable-sha256"
PACKAGECONFIG[libsolv] = "--with-libsolv,--without-libsolv,libsolv"

EXTRA_OECONF:class-native = "--localstatedir=/${@os.path.relpath('${localstatedir}', '${STAGING_DIR_NATIVE}')} --sysconfdir=/${@os.path.relpath('${sysconfdir}', '${STAGING_DIR_NATIVE}')}"

do_install:append () {
	install -d ${D}${sysconfdir}/opkg
	install -m 0644 ${WORKDIR}/opkg.conf ${D}${sysconfdir}/opkg/opkg.conf
	echo "option lists_dir   ${OPKGLIBDIR}/opkg/lists"  >>${D}${sysconfdir}/opkg/opkg.conf
	echo "option info_dir    ${OPKGLIBDIR}/opkg/info"   >>${D}${sysconfdir}/opkg/opkg.conf
	echo "option status_file ${OPKGLIBDIR}/opkg/status" >>${D}${sysconfdir}/opkg/opkg.conf

	# We need to create the lock directory
	install -d ${D}${OPKGLIBDIR}/opkg
}

do_install_ptest () {
	sed -i -e '/@echo $^/d' ${D}${PTEST_PATH}/tests/Makefile
	sed -i -e '/@PYTHONPATH=. $(PYTHON) $^/a\\t@if [ "$$?" != "0" ];then echo "FAIL:"$^;else echo "PASS:"$^;fi' ${D}${PTEST_PATH}/tests/Makefile
}

RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_update-alternatives} opkg-arch-config libarchive"
RDEPENDS:${PN}:class-native = ""
RDEPENDS:${PN}:class-nativesdk = ""
RDEPENDS:${PN}-ptest += "make binutils python3-core python3-compression"
RREPLACES:${PN} = "opkg-nogpg opkg-collateral"
RCONFLICTS:${PN} = "opkg-collateral"
RPROVIDES:${PN} = "opkg-collateral"

FILES:libopkg = "${libdir}/*.so.* ${OPKGLIBDIR}/opkg/"

BBCLASSEXTEND = "native nativesdk"

CONFFILES:${PN} = "${sysconfdir}/opkg/opkg.conf"
