SUMMARY = "International Component for Unicode libraries"
DESCRIPTION = "The International Component for Unicode (ICU) is a mature, \
portable set of C/C++ and Java libraries for Unicode support, software \
internationalization (I18N) and globalization (G11N), giving applications the \
same results on all platforms."
HOMEPAGE = "https://icu.unicode.org/"

LICENSE = "ICU & MIT"
DEPENDS = "icu-native"

CVE_PRODUCT = "international_components_for_unicode"

S = "${UNPACKDIR}/icu/source"
STAGING_ICU_DIR_NATIVE = "${STAGING_DATADIR_NATIVE}/${BPN}/${PV}"

ICU_MAJOR_VER = "${@d.getVar('PV').split('.')[0]}"

inherit autotools pkgconfig github-releases

# ICU needs the native build directory as an argument to its --with-cross-build option when
# cross-compiling. Taken the situation that different builds may share a common sstate-cache
# into consideration, the native build directory needs to be staged.
EXTRA_OECONF = "--with-cross-build=${STAGING_ICU_DIR_NATIVE} --disable-icu-config ac_cv_path_install='install -c'"
EXTRA_OECONF:class-native = "--disable-icu-config ac_cv_path_install='install -c'"
EXTRA_OECONF:class-nativesdk = "--with-cross-build=${STAGING_ICU_DIR_NATIVE} --disable-icu-config ac_cv_path_install='install -c'"
EXTRA_OECONF:append:class-target = " --enable-automake-test-format"
EXTRA_OECONF:append:class-target = "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'be', ' --with-data-packaging=archive', '', d)}"
TARGET_CXXFLAGS:append = "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'be', ' -DICU_DATA_DIR=\\""${datadir}/${BPN}/${PV}\\""', '', d)}"

ASNEEDED = ""

remove_build_host_references_from_libicutu () {
	# Make sure certain build host references do not end up being compiled
	# in the image. This only affects libicutu and icu-dbg
	sed  \
	    -e 's,DU_BUILD=,DU_BUILD_unused=,g' \
	    -e '/^CPPFLAGS.*/ s,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -i ${B}/tools/toolutil/Makefile
}

do_compile:prepend:class-target () {
	remove_build_host_references_from_libicutu
}

do_compile:prepend:class-nativesdk () {
	remove_build_host_references_from_libicutu
}

PREPROCESS_RELOCATE_DIRS = "${datadir}/${BPN}/${PV}"
do_install:append:class-native() {
	mkdir -p ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/config/icucross.mk ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/config/icucross.inc ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/lib ${D}/${STAGING_ICU_DIR_NATIVE}
	cp -r ${B}/bin ${D}/${STAGING_ICU_DIR_NATIVE}
	cp -r ${B}/tools ${D}/${STAGING_ICU_DIR_NATIVE}
}

remove_build_host_references() {
	sed -i  \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    ${D}/${libdir}/${BPN}/${PV}/Makefile.inc \
	    ${D}/${libdir}/${BPN}/${PV}/pkgdata.inc
}

do_install:append:class-target() {
    # The native pkgdata can not generate the correct data file.
    # Use icupkg to re-generate it.
    if [ "${SITEINFO_ENDIANNESS}" = "be" ] ; then
        rm -f ${D}/${datadir}/${BPN}/${PV}/icudt${ICU_MAJOR_VER}b.dat
        icupkg -tb ${S}/data/in/icudt${ICU_MAJOR_VER}l.dat ${D}/${datadir}/${BPN}/${PV}/icudt${ICU_MAJOR_VER}b.dat
    fi

	remove_build_host_references
}

do_install:append:class-nativesdk() {
	remove_build_host_references
}

PACKAGES =+ "libicudata libicuuc libicui18n libicutu libicuio"

FILES:${PN}-dev += "${libdir}/${BPN}/"

FILES:libicudata = "${libdir}/libicudata.so.*"
FILES:libicuuc = "${libdir}/libicuuc.so.*"
FILES:libicui18n = "${libdir}/libicui18n.so.*"
FILES:libicutu = "${libdir}/libicutu.so.*"
FILES:libicuio = "${libdir}/libicuio.so.*"

BBCLASSEXTEND = "native nativesdk"

LIC_FILES_CHKSUM = "file://../LICENSE;md5=e531a388be7c1df9a0fb7b4010c9c1d7"

ICU_PV = "${PV}"
ICU_FOLDER = "${PV}"

# http://errors.yoctoproject.org/Errors/Details/20486/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

BASE_SRC_URI = "${GITHUB_BASE_URI}/download/release-${ICU_FOLDER}/icu4c-${ICU_PV}-sources.tgz"
DATA_SRC_URI = "${GITHUB_BASE_URI}/download/release-${ICU_FOLDER}/icu4c-${ICU_PV}-data.zip"
SRC_URI = "${BASE_SRC_URI};name=code \
           ${DATA_SRC_URI};name=data \
           file://filter.json \
           file://0001-icu-Added-armeb-support.patch \
           file://0001-ICU-23120-Mask-UnicodeStringTest-TestLargeMemory-on-.patch \
           file://0001-test-Add-support-ptest.patch \
           file://run-ptest \
           file://0001-Make-ICU-test-output-compatible-with-Automake-format.patch \
          "

SRC_URI:append:class-target = "\
           file://0001-Disable-LDFLAGSICUDT-for-Linux.patch \
          "
SRC_URI[code.sha256sum] = "3e99687b5c435d4b209630e2d2ebb79906c984685e78635078b672e03c89df35"
SRC_URI[data.sha256sum] = "582968cf174c9498b2046b4f4e7f786def5f18222bd8d98432d7a29399c38c70"

UPSTREAM_CHECK_REGEX = "releases/tag/release-(?P<pver>(?!.+rc).+)"
GITHUB_BASE_URI = "https://github.com/unicode-org/icu/releases"

EXTRA_OECONF:append:libc-musl = " ac_cv_func_strtod_l=no"

PACKAGECONFIG ?= ""
PACKAGECONFIG[make-icudata] = ",,,"

do_make_icudata:class-target () {
    ${@bb.utils.contains('PACKAGECONFIG', 'make-icudata', '', 'exit 0', d)}
    cd ${S}
    rm -rf data
    cp -a ${UNPACKDIR}/data .
    AR='${BUILD_AR}' \
    CC='${BUILD_CC}' \
    CPP='${BUILD_CPP}' \
    CXX='${BUILD_CXX}' \
    RANLIB='${BUILD_RANLIB}' \
    CFLAGS='${BUILD_CFLAGS}' \
    CPPFLAGS='${BUILD_CPPFLAGS}' \
    CXXFLAGS='${BUILD_CXXFLAGS}' \
    LDFLAGS='${BUILD_LDFLAGS}' \
    ICU_DATA_FILTER_FILE=${UNPACKDIR}/filter.json \
    ./runConfigureICU Linux --with-data-packaging=archive
    oe_runmake
    install -Dm644 ${S}/data/out/icudt${ICU_MAJOR_VER}l.dat ${S}/data/in/icudt${ICU_MAJOR_VER}l.dat
}

do_make_icudata() {
    :
}

addtask make_icudata before do_configure after do_patch do_prepare_recipe_sysroot

inherit ptest
RDEPENDS:${PN}-ptest += "bash"

do_compile_ptest() {
    oe_runmake -C test everything PTEST_PATH=${PTEST_PATH}
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    install -d ${D}${PTEST_PATH}/data
    cp -r ${S}/test/testdata ${D}/${PTEST_PATH}/test
    cp -r ${S}/data/unidata ${D}/${PTEST_PATH}/data/
    cp -r ${S}/data/sprep ${D}/${PTEST_PATH}/data/
    cp -r ${S}/../testdata ${D}/${PTEST_PATH}/
    cp -r ${B}/test/testdata/out ${D}/${PTEST_PATH}/test/testdata

    install -d ${D}${PTEST_PATH}/test/tests
    find ${B}/test/ -type f -executable -exec cp {} ${D}${PTEST_PATH}/test/tests \;
}

CVE_STATUS[CVE-2025-5222] = "fixed-version: fixed in version 77-1"
