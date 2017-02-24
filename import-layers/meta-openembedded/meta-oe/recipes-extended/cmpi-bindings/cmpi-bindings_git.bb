SUMMARY = "Adapter to write and run CMPI-type CIM providers"
DESCRIPTION = "CMPI-compliant provider interface for various languages via SWIG"
HOMEPAGE = "http://github.com/kkaempf/cmpi-bindings"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b19ee058d2d5f69af45da98051d91064"
SECTION = "Development/Libraries"
DEPENDS = "swig-native python3 sblim-cmpi-devel"

SRC_URI = "git://github.com/kkaempf/cmpi-bindings.git;protocol=http \
           file://cmpi-bindings-0.4.17-no-ruby-perl.patch \
           file://cmpi-bindings-0.4.17-sblim-sigsegv.patch \
           file://cmpi-bindings-0.9.5-python-lib-dir.patch \
           file://0001-Modify-cmakelist.patch \
           file://0001-Fix-error.patch \
           file://0001-fix-the-build-error-when-python-3.0.patch \
           file://0001-Add-PYTHON_ABI-suffix-to-the-python-library-name.patch \
          "

SRCREV = "62f60e065aa1b901f826e4f530c0573ae32d065e"
S = "${WORKDIR}/git"

inherit cmake python3native

EXTRA_OECMAKE = "-DLIB='${baselib}' \
                 -DPYTHON_INCLUDE_PATH=${STAGING_INCDIR}/python${PYTHON_BASEVERSION}"

do_configure_prepend() {
    export STAGING_LIBDIR=${STAGING_LIBDIR}
    export STAGING_INCDIR=${STAGING_INCDIR}
    export ENV_INSTALL_PATH=${PYTHON_SITEPACKAGES_DIR}
}

FILES_${PN} =+"${libdir}/cmpi/libpyCmpiProvider.so ${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-dbg =+ "${libdir}/cmpi/.debug/libpyCmpiProvider.so"

BBCLASSEXTEND = "native"
