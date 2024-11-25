# allarch is required because the files this recipe produces (html and
# javascript) are valid for any target, regardless of architecture.  The allarch
# class removes your compiler definitions, as it assumes that anything that
# requires a compiler is platform specific.  Unfortunately, one of the build
# tools uses libsass for compiling the css templates, and it needs a compiler to
# build the library that it then uses to compress the scss into normal css.
# Enabling allarch, then re-adding the compiler flags was the best of the bad
# options
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS:prepend = "nodejs-native "
SRCREV = "b44986729febcefc75a669d16dda5d081473b3aa"
PV = "1.0+git${SRCPV}"
# This recipe requires online access to build, as it uses NPM for dependency
# management and resolution.
PR = "r1"

SRC_URI = "git://github.com/openbmc/webui-vue.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit allarch

# Ideally this recipe would use npm.bbclass, but it doesn't.  Since npm uses
# python3, we need to inherit this to get the Yocto version of python3 instead
# of the hosttools one.
inherit python3native

RDEPENDS:${PN}:append = " bmcweb"

EXTRA_OENPM ?= ""

export CXX = "${BUILD_CXX}"
export CC = "${BUILD_CC}"
export CFLAGS = "${BUILD_CFLAGS}"
export CPPFLAGS = "${BUILD_CPPFLAGS}"
export CXXFLAGS = "${BUILD_CXXFLAGS}"

# Workaround
# Network access from task are disabled by default on Yocto 3.5
# https://git.yoctoproject.org/poky/tree/documentation/migration-guides/migration-3.5.rst#n25
do_compile[network] = "1"
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

FILES:${PN} += "${datadir}/www/*"
