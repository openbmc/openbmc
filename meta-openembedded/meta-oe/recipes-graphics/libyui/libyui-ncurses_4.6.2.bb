SUMMARY = "Character Based User Interface for libyui"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://../COPYING.lgpl-3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://../COPYING.lgpl-2.1;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = "git://github.com/libyui/libyui.git;branch=master;protocol=https \
          "

SRC_URI:append:class-target = " file://0001-Fix-the-error-of-can-t-find-header-file.patch"

SRCREV = "e691b563262306a40a61bbf228839fa06e4f0516"

S = "${WORKDIR}/git/libyui-ncurses"

inherit cmake gettext pkgconfig

DEPENDS += "boost libyui ncurses"

BBCLASSEXTEND = "nativesdk"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DWERROR=OFF -DCMAKE_SKIP_RPATH=1"

# TODO: Check with newer GCC, it works ok with GCC
# GCC-14 causes buildpaths QA error
# WARNING: libyui-ncurses-4.6.2-r0 do_package_qa: QA Issue: File /usr/lib/yui/.debug/libyui-ncurses.so.16.0.0 in package libyui-ncurses-dbg contains reference to TMPDIR [buildpaths

DEBUG_LEVELFLAG = ""

CXXFLAGS += "-DNCURSES_WIDECHAR"
LDFLAGS += "-L${B}/src"

do_configure:prepend () {
    cd ${S}
    if [ -e ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib/libyui.so ]; then
        mkdir -p ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib64/
        cp ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib/libyui.so* ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib64/
    fi
    cd -
    sed -i -e "s#\${YPREFIX}#\${PKG_CONFIG_SYSROOT_DIR}${base_prefix}&#" ${S}/CMakeLists.txt
}

do_install:append () {
    if [ "${libdir}" = "${base_prefix}/usr/lib" ] && [ -d ${D}/usr/lib64 ]; then
        mv ${D}/usr/lib64 ${D}/usr/lib
    fi
}

do_install:append:class-nativesdk () {
    mkdir -p ${D}/${base_prefix}
    mv ${D}/usr ${D}/${base_prefix}
}

FILES:${PN} += "${datadir}/*"

FILES:${PN}-dev += "${libdir}/*"
