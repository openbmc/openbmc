SUMMARY = "Open Package Manager"
SUMMARY:libopkg = "Open Package Manager library"
SECTION = "base"
HOMEPAGE = "https://git.yoctoproject.org/opkg/"
DESCRIPTION = "Opkg is a lightweight package management system based on Ipkg."
BUGTRACKER = "https://bugzilla.yoctoproject.org/buglist.cgi?quicksearch=Product%3Aopkg"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/opkg.c;beginline=4;endline=18;md5=d6200b0f2b41dee278aa5fad333eecae"

DEPENDS = "libarchive zstd"

PE = "1"

SRC_URI = "http://downloads.yoctoproject.org/releases/${BPN}/${BPN}-${PV}.tar.gz \
           file://opkg.conf \
           file://0001-opkg_conf-create-opkg.lock-in-run-instead-of-var-run.patch \
           file://0001-libopkg-Use-libgen.h-to-provide-basename-API.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "f3938e359646b406c40d5d442a1467c7e72357f91ab822e442697529641e06de"

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

EXTRA_OECONF = "--enable-zstd"
EXTRA_OECONF:append:class-native = " --localstatedir=/${@os.path.relpath('${localstatedir}', '${STAGING_DIR_NATIVE}')} --sysconfdir=/${@os.path.relpath('${sysconfdir}', '${STAGING_DIR_NATIVE}')}"

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

WARN_QA:append = " internal-solver-deprecation"
QARECIPETEST[internal-solver-deprecation] = "qa_check_solver_deprecation"
def qa_check_solver_deprecation (pn, d, messages):
    pkgconfig = (d.getVar("PACKAGECONFIG") or "").split()

    if "libsolv" not in pkgconfig:
        oe.qa.handle_error("internal-solver-deprecation", "The opkg internal solver will be deprecated in future opkg releases. Consider enabling \"libsolv\" in PACKAGECONFIG.", d)


RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_update-alternatives} opkg-arch-config libarchive"
RDEPENDS:${PN}:class-native = ""
RDEPENDS:${PN}:class-nativesdk = ""
RDEPENDS:${PN}-ptest += "make binutils python3-core python3-compression bash python3-crypt python3-io"
RREPLACES:${PN} = "opkg-nogpg opkg-collateral"
RCONFLICTS:${PN} = "opkg-collateral"
RPROVIDES:${PN} = "opkg-collateral"

FILES:libopkg = "${libdir}/*.so.* ${OPKGLIBDIR}/opkg/"

BBCLASSEXTEND = "native nativesdk"

CONFFILES:${PN} = "${sysconfdir}/opkg/opkg.conf"
