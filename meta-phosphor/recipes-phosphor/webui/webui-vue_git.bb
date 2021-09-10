# This recipe requires online access to build, as it uses NPM for dependency
# management and resolution.
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "git://github.com/openbmc/webui-vue.git"
SRCREV = "aaff26ef8262df7d6b5bfdd5da52b75a158a4ec5"
S = "${WORKDIR}/git"

DEPENDS:prepend = "nodejs-native nlf-native "

# allarch is required because the files this recipe produces (html and
# javascript) are valid for any target, regardless of architecture.  The allarch
# class removes your compiler definitions, as it assumes that anything that
# requires a compiler is platform specific.  Unfortunately, one of the build
# tools uses libsass for compiling the css templates, and it needs a compiler to
# build the library that it then uses to compress the scss into normal css.
# Enabling allarch, then re-adding the compiler flags was the best of the bad
# options

inherit allarch

export CXX = "${BUILD_CXX}"
export CC = "${BUILD_CC}"
export CFLAGS = "${BUILD_CFLAGS}"
export CPPFLAGS = "${BUILD_CPPFLAGS}"
export CXXFLAGS = "${BUILD_CXXFLAGS}"

FILES:${PN} += "${datadir}/www/*"

EXTRA_OENPM ?= ""

do_compile () {
    cd ${S}
    rm -rf node_modules
    npm --loglevel info --proxy=${http_proxy} --https-proxy=${https_proxy} install
    npm run build ${EXTRA_OENPM}
}

do_install () {
   # create directory structure
   install -d ${D}${datadir}/www
   cp -r ${S}/dist/** ${D}${datadir}/www
   find ${D}${datadir}/www -type f -exec chmod a=r,u+w '{}' +
   find ${D}${datadir}/www -type d -exec chmod a=rx,u+w '{}' +
}

do_find_node_licenses() {
    cd ${S}
    nlf -s detail > ${LICENSE_DIRECTORY}/${PN}/node-licenses
}

addtask find_node_licenses after do_compile before do_build
