SUMMARY = "C implementation of createrepo."
HOMEPAGE = "https://github.com/rpm-software-management/createrepo_c/wiki"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/rpm-software-management/createrepo_c;branch=master;protocol=https;tag=${PV} \
           file://0001-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           file://0001-include-rpm-rpmstring.h.patch \
           file://0001-Fix-libname-of-Libs.private.patch \
           file://0002-Use-IMPORTED_TARGET-for-3rd-party-dependencies.patch \
           file://0003-Don-t-try-to-use-imported-targets-of-turned-off-depe.patch \
           file://0004-cmake-Allow-builds-without-Doxygen-being-present-wit.patch \
           "

SRCREV = "8c6e6f88df86d1e34ca26d3835d77a2816326414"

DEPENDS = "expat curl glib-2.0 libxml2 openssl bzip2 zlib file sqlite3 xz rpm"
DEPENDS:append:class-native = " file-replacement-native"

inherit cmake pkgconfig bash-completion setuptools3-base

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3 -DWITH_ZCHUNK=OFF -DENABLE_DRPM=OFF -DWITH_LIBMODULEMD=OFF"

BBCLASSEXTEND = "native nativesdk"

# Direct createrepo to read rpm configuration from our sysroot, not the one it was compiled in
do_install:append:class-native() {
        create_wrapper ${D}/${bindir}/createrepo_c \
                RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm \
                MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc
        create_wrapper ${D}/${bindir}/modifyrepo_c \
                MAGIC=${STAGING_DIR_NATIVE}${datadir_native}/misc/magic.mgc
}

do_install:append:class-nativesdk() {
        create_wrapper ${D}/${bindir}/createrepo_c \
                RPM_CONFIGDIR=${SDKPATHNATIVE}${libdir_nativesdk}/rpm \
                MAGIC=${datadir}/misc/magic.mgc
        create_wrapper ${D}/${bindir}/modifyrepo_c \
                MAGIC=${datadir}/misc/magic.mgc
        rm -rf ${D}/etc
}
