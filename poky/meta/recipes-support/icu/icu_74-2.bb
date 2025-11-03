SUMMARY = "International Component for Unicode libraries"
DESCRIPTION = "The International Component for Unicode (ICU) is a mature, \
portable set of C/C++ and Java libraries for Unicode support, software \
internationalization (I18N) and globalization (G11N), giving applications the \
same results on all platforms."
HOMEPAGE = "http://site.icu-project.org/"

LICENSE = "ICU"
DEPENDS = "icu-native autoconf-archive-native"

CVE_PRODUCT = "international_components_for_unicode"

S = "${WORKDIR}/icu/source"
SPDX_S = "${WORKDIR}/icu"
STAGING_ICU_DIR_NATIVE = "${STAGING_DATADIR_NATIVE}/${BPN}/${PV}"

ICU_MAJOR_VER = "${@d.getVar('PV').split('-')[0]}"

inherit autotools pkgconfig github-releases

# ICU needs the native build directory as an argument to its --with-cross-build option when
# cross-compiling. Taken the situation that different builds may share a common sstate-cache
# into consideration, the native build directory needs to be staged.
EXTRA_OECONF = "--with-cross-build=${STAGING_ICU_DIR_NATIVE} --disable-icu-config"
EXTRA_OECONF:class-native = "--disable-icu-config"
EXTRA_OECONF:class-nativesdk = "--with-cross-build=${STAGING_ICU_DIR_NATIVE} --disable-icu-config"

EXTRA_OECONF:append:class-target = "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'be', ' --with-data-packaging=archive', '', d)}"
TARGET_CXXFLAGS:append = "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'be', ' -DICU_DATA_DIR=\\""${datadir}/${BPN}/${@icu_install_folder(d)}\\""', '', d)}"

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
	    ${D}/${libdir}/${BPN}/${@icu_install_folder(d)}/Makefile.inc \
	    ${D}/${libdir}/${BPN}/${@icu_install_folder(d)}/pkgdata.inc
}

do_install:append:class-target() {
    # The native pkgdata can not generate the correct data file.
    # Use icupkg to re-generate it.
    if [ "${SITEINFO_ENDIANNESS}" = "be" ] ; then
        rm -f ${D}/${datadir}/${BPN}/${@icu_install_folder(d)}/icudt${ICU_MAJOR_VER}b.dat
        icupkg -tb ${S}/data/in/icudt${ICU_MAJOR_VER}l.dat ${D}/${datadir}/${BPN}/${@icu_install_folder(d)}/icudt${ICU_MAJOR_VER}b.dat
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

LIC_FILES_CHKSUM = "file://../LICENSE;md5=08dc3852df8fffa807301902ad899ff8"

def icu_download_version(d):
    pvsplit = d.getVar('PV').split('-')
    return pvsplit[0] + "_" + pvsplit[1]

def icu_download_folder(d):
    pvsplit = d.getVar('PV').split('-')
    return pvsplit[0] + "-" + pvsplit[1]

def icu_install_folder(d):
    pvsplit = d.getVar('PV').split('-')
    return pvsplit[0] + "." + pvsplit[1]

ICU_PV = "${@icu_download_version(d)}"
ICU_FOLDER = "${@icu_download_folder(d)}"

# http://errors.yoctoproject.org/Errors/Details/20486/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

BASE_SRC_URI = "${GITHUB_BASE_URI}/download/release-${ICU_FOLDER}/icu4c-${ICU_PV}-src.tgz"
DATA_SRC_URI = "${GITHUB_BASE_URI}/download/release-${ICU_FOLDER}/icu4c-${ICU_PV}-data.zip"
SRC_URI = "${BASE_SRC_URI};name=code \
           ${DATA_SRC_URI};name=data \
           file://filter.json \
           file://fix-install-manx.patch \
           file://0001-icu-Added-armeb-support.patch \
           file://CVE-2025-5222.patch \
           file://ICU-22813_rise_buffer_sizes_pkgdata_PR3058.patch \
           "

SRC_URI:append:class-target = "\
           file://0001-Disable-LDFLAGSICUDT-for-Linux.patch \
          "
SRC_URI[code.sha256sum] = "68db082212a96d6f53e35d60f47d38b962e9f9d207a74cfac78029ae8ff5e08c"
SRC_URI[data.sha256sum] = "c28c3ca5f4ba3384781797138a294ca360988d4322674ad4d51e52f5d9b0a2b6"

UPSTREAM_CHECK_REGEX = "releases/tag/release-(?P<pver>(?!.+rc).+)"
GITHUB_BASE_URI = "https://github.com/unicode-org/icu/releases"

EXTRA_OECONF:append:libc-musl = " ac_cv_func_strtod_l=no"

PACKAGECONFIG ?= ""
PACKAGECONFIG[make-icudata] = ",,,"

do_make_icudata:class-target () {
    ${@bb.utils.contains('PACKAGECONFIG', 'make-icudata', '', 'exit 0', d)}
    cd ${S}
    rm -rf data
    cp -a ${WORKDIR}/data .
    AR='${BUILD_AR}' \
    CC='${BUILD_CC}' \
    CPP='${BUILD_CPP}' \
    CXX='${BUILD_CXX}' \
    RANLIB='${BUILD_RANLIB}' \
    CFLAGS='${BUILD_CFLAGS}' \
    CPPFLAGS='${BUILD_CPPFLAGS}' \
    CXXFLAGS='${BUILD_CXXFLAGS}' \
    LDFLAGS='${BUILD_LDFLAGS}' \
    ICU_DATA_FILTER_FILE=${WORKDIR}/filter.json \
    ./runConfigureICU Linux --with-data-packaging=archive
    oe_runmake
    install -Dm644 ${S}/data/out/icudt${ICU_MAJOR_VER}l.dat ${S}/data/in/icudt${ICU_MAJOR_VER}l.dat
}

do_make_icudata() {
    :
}

addtask make_icudata before do_configure after do_patch do_prepare_recipe_sysroot
