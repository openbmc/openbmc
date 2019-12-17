DESCRIPTION = "C implementation of createrepo."
HOMEPAGE = "https://github.com/rpm-software-management/createrepo_c/wiki"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/rpm-software-management/createrepo_c \
           file://0001-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           "

SRCREV = "bc67f19ed09593e3886ebeee2391e9d51cc3439f"

S = "${WORKDIR}/git"

DEPENDS = "expat curl glib-2.0 libxml2 openssl bzip2 zlib file sqlite3 xz rpm"
DEPENDS_append_class-native = " file-replacement-native"

inherit cmake pkgconfig bash-completion distutils3-base

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3 -DWITH_ZCHUNK=OFF -DENABLE_DRPM=OFF -DWITH_LIBMODULEMD=OFF"

BBCLASSEXTEND = "native nativesdk"

# Direct createrepo to read rpm configuration from our sysroot, not the one it was compiled in
do_install_append_class-native() {
        create_wrapper ${D}/${bindir}/createrepo_c \
                RPM_CONFIGDIR=${STAGING_LIBDIR_NATIVE}/rpm
}

do_install_append_class-nativesdk() {
        create_wrapper ${D}/${bindir}/createrepo_c \
                RPM_CONFIGDIR=${SDKPATHNATIVE}${libdir_nativesdk}/rpm
        rm -rf ${D}/etc
}
