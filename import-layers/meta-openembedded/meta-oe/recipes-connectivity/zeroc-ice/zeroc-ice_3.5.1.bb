DESCRIPTION = "The Internet Communications Engine"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://ICE_LICENSE;md5=3dc3037023cc2ae6b2c5b995da529515"
DEPENDS = "bzip2 expat openssl python db mcpp"
DEPENDS_prepend_class-target = "zeroc-ice-native "

SRC_URI = "http://www.zeroc.com/download/Ice/3.5/Ice-${PV}.tar.gz \
           file://0002-Modify-Makefile-for-cross-compile.patch \
          "
SRC_URI[md5sum] = "f00c59983cc904bca977133c0a9b3e80"
SRC_URI[sha256sum] = "989e51194c6adadbd156da3288e37bad847b93b3b876502e83033b70493af392"

#| MapDb.cpp: In constructor 'Freeze::MapDb::MapDb(const ConnectionIPtr&, const string&, const string&, const string&, const KeyCompareBasePtr&, const std::vector<IceUtil::Handle<Freeze::MapIndexBase> >&, bool)':
#| MapDb.cpp:138:46: error: call of overloaded 'set_bt_compare(int (*)(DB*, const DBT*, const DBT*))' is ambiguous
#| MapDb.cpp:138:46: note: candidates are:
#| /home/jenkins/oe/world/shr-core/tmp-eglibc/sysroots/x86_64-linux/usr/include/db_cxx.h:272:14: note: virtual int Db::set_bt_compare(bt_compare_fcn_type) <near match>
#| /home/jenkins/oe/world/shr-core/tmp-eglibc/sysroots/x86_64-linux/usr/include/db_cxx.h:272:14: note:   no known conversion for argument 1 from 'int (*)(DB*, const DBT*, const DBT*) {aka int (*)(__db*, const __db_dbt*, const __db_dbt*)}' to 'bt_compare_fcn_type {aka int (*)(__db*, const __db_dbt*, const __db_dbt*, long unsigned int*)}'
#| /home/jenkins/oe/world/shr-core/tmp-eglibc/sysroots/x86_64-linux/usr/include/db_cxx.h:273:14: note: virtual int Db::set_bt_compare(int (*)(Db*, const Dbt*, const Dbt*, size_t*)) <near match>
#| /home/jenkins/oe/world/shr-core/tmp-eglibc/sysroots/x86_64-linux/usr/include/db_cxx.h:273:14: note:   no known conversion for argument 1 from 'int (*)(DB*, const DBT*, const DBT*) {aka int (*)(__db*, const __db_dbt*, const __db_dbt*)}' to 'int (*)(Db*, const Dbt*, const Dbt*, size_t*) {aka int (*)(Db*, const Dbt*, const Dbt*, long unsigned int*)}'
#| make[3]: *** [MapDb.o] Error 1
PNBLACKLIST[zeroc-ice] ?= "BROKEN: not compatible with default db version"

S = "${WORKDIR}/Ice-${PV}"

inherit python-dir pkgconfig

export PYTHON_VERSION = "python2.7"

do_configure() {
    :
}

do_compile_prepend_class-target () {
    export PYTHON_INCLUDE_DIR="${STAGING_INCDIR}/${PYTHON_DIR}"
    export PYTHON_LIB_DIR="${STAGING_LIBDIR}/${PYTHON_DIR}"
    export ICE_HOME="${STAGING_DIR_NATIVE}/usr"
}

do_compile_prepend_class-nativesdk () {
    export PYTHON_INCLUDE_DIR="${STAGING_INCDIR}/${PYTHON_DIR}"
    export PYTHON_LIB_DIR="${STAGING_LIBDIR}/${PYTHON_DIR}"
    export ICE_HOME="${STAGING_DIR_NATIVE}/usr"
}

do_compile_prepend_class-native () {
    export PYTHON_INCLUDE_DIR="${STAGING_INCDIR}/${PYTHON_DIR}"
    export PYTHON_LIB_DIR="${STAGING_LIBDIR}/${PYTHON_DIR}"
    export ICE_HOME="${S}/cpp"
}

do_compile() {
    oe_runmake -C ${S} cpp
    oe_runmake -C ${S} py
}

do_install_prepend_class-target () {
    export PYTHON_INCLUDE_DIR="${STAGING_INCDIR}/${PYTHON_DIR}"
    export PYTHON_LIB_DIR="${STAGING_LIBDIR}/${PYTHON_DIR}"
    export ICE_HOME="${STAGING_DIR_NATIVE}/usr"
}

do_install_prepend_class-nativesdk () {
    export PYTHON_INCLUDE_DIR="${STAGING_INCDIR}/${PYTHON_DIR}"
    export PYTHON_LIB_DIR="${STAGING_LIBDIR}/${PYTHON_DIR}"
    export ICE_HOME="${STAGING_DIR_NATIVE}/usr"
}

do_install_prepend_class-native () {
    export ICE_HOME="${S}/cpp"
}

do_install() {
    oe_runmake -C ${S}/cpp prefix=${D}${prefix} install install-common
    oe_runmake -C ${S}/py prefix=${D}${prefix} install
}

PACKAGES += "${PN}-python ${PN}-python-dev ${PN}-python-dbg"

FILES_${PN}-doc += "${prefix}/man/man1"
FILES_${PN} += "${prefix}/*LICENSE ${libdir}/ImportKey.class ${prefix}/RELEASE_NOTES ${prefix}/CHANGES"
FILES_${PN}-dev += "${includedir} ${prefix}/slice ${prefix}/config"
FILES_${PN}-python-dev = "${PYTHON_SITEPACKAGES_DIR}/IcePy.so"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*.py ${PYTHON_SITEPACKAGES_DIR}/IcePy.so.* ${PYTHON_SITEPACKAGES_DIR}/Ice*/*.py"
FILES_${PN}-python-dbg = "${PYTHON_SITEPACKAGES_DIR}/.debug"
FILES_${PN}-dev += "${bindir}/slice* ${datadir}/Ice-${PV}/slice/*"

BBCLASSEXTEND = "native nativesdk"
