SUMMARY = "International Component for Unicode libraries"
DESCRIPTION = "The International Component for Unicode (ICU) is a mature, \
portable set of C/C++ and Java libraries for Unicode support, software \
internationalization (I18N) and globalization (G11N), giving applications the \
same results on all platforms."
HOMEPAGE = "http://site.icu-project.org/"

LICENSE = "ICU"
DEPENDS = "icu-native"
DEPENDS_class-native = ""

CVE_PRODUCT = "international_components_for_unicode"

S = "${WORKDIR}/icu/source"
SPDX_S = "${WORKDIR}/icu"
STAGING_ICU_DIR_NATIVE = "${STAGING_DATADIR_NATIVE}/${BPN}/${PV}"

BINCONFIG = "${bindir}/icu-config"

inherit autotools pkgconfig binconfig

# ICU needs the native build directory as an argument to its --with-cross-build option when
# cross-compiling. Taken the situation that different builds may share a common sstate-cache
# into consideration, the native build directory needs to be staged.
EXTRA_OECONF = "--with-cross-build=${STAGING_ICU_DIR_NATIVE}"
EXTRA_OECONF_class-native = ""
EXTRA_OECONF_class-nativesdk = "--with-cross-build=${STAGING_ICU_DIR_NATIVE}"


# strtod_l() is not supported by musl; also xlocale.h is missing
# It is not possible to disable its use via configure switches or env vars
# so monkey patching is needed.
do_configure_prepend_libc-musl () {
    sed -i -e 's,DU_HAVE_STRTOD_L=1,DU_HAVE_STRTOD_L=0,' ${S}/configure.ac
}

PREPROCESS_RELOCATE_DIRS = "${datadir}/${BPN}/${PV}"
do_install_append_class-native() {
	mkdir -p ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/config/icucross.mk ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/config/icucross.inc ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/lib ${D}/${STAGING_ICU_DIR_NATIVE}
	cp -r ${B}/bin ${D}/${STAGING_ICU_DIR_NATIVE}
	cp -r ${B}/tools ${D}/${STAGING_ICU_DIR_NATIVE}
}

PACKAGES =+ "libicudata libicuuc libicui18n libicutu libicuio"

FILES_${PN}-dev += "${libdir}/${BPN}/"

FILES_libicudata = "${libdir}/libicudata.so.*"
FILES_libicuuc = "${libdir}/libicuuc.so.*"
FILES_libicui18n = "${libdir}/libicui18n.so.*"
FILES_libicutu = "${libdir}/libicutu.so.*"
FILES_libicuio = "${libdir}/libicuio.so.*"

BBCLASSEXTEND = "native nativesdk"
