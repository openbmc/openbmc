SUMMARY = "Adapter to write and run CMPI-type CIM providers"
DESCRIPTION = "CMPI-compliant provider interface for various languages via SWIG"
HOMEPAGE = "http://github.com/kkaempf/cmpi-bindings"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b19ee058d2d5f69af45da98051d91064"
SECTION = "Development/Libraries"
DEPENDS = "swig-native sblim-cmpi-devel python3-setuptools-native"

SRC_URI = "git://github.com/kkaempf/cmpi-bindings.git;protocol=https;branch=master \
           file://cmpi-bindings-0.4.17-no-ruby-perl.patch \
           file://cmpi-bindings-0.4.17-sblim-sigsegv.patch \
           file://0001-Fix-error.patch \
           file://0001-cmpi-bindings-Fix-build-error-with-gcc14.patch \
           "

SRCREV = "69077ee4d249816ed428155fc933dca424167e77"
S = "${WORKDIR}/git"

inherit cmake python3targetconfig

EXTRA_OECMAKE = "-DLIB='${baselib}' \
                 -DPYTHON_INCLUDE_PATH=${STAGING_INCDIR}/python${PYTHON_BASEVERSION} \
                 -DPYTHON_ABI=${PYTHON_ABI} \
                 -DBUILD_PYTHON3=YES \
                 -DPython3_SITE_DIR=${PYTHON_SITEPACKAGES_DIR} \
                 "

# With Ninja it fails with:
# ninja: error: build.ninja:282: bad $-escape (literal $ must be written as $$)
OECMAKE_GENERATOR = "Unix Makefiles"

FILES:${PN} =+"${libdir}/cmpi/libpy3CmpiProvider.so ${PYTHON_SITEPACKAGES_DIR}/*"
FILES:${PN}-dbg =+ "${libdir}/cmpi/.debug/libpyCmpiProvider.so"

BBCLASSEXTEND = "native"
