SUMMARY = "Character Based User Interface for libyui"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://../COPYING.lgpl-3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://../COPYING.lgpl-2.1;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = "git://github.com/libyui/libyui.git;branch=master;protocol=https \
           file://0001-libyui-ncurses-Replace-off64_t-with-off_t-and-stat64.patch \
          "

SRC_URI:append:class-target = " file://0001-Fix-the-error-of-can-t-find-header-file.patch"

SRCREV = "dabdcd9cc6a0058fd6966d7d2e19d508debcc0ac"

S = "${WORKDIR}/git/libyui-ncurses"

inherit cmake gettext pkgconfig

DEPENDS += "boost libyui ncurses"

BBCLASSEXTEND = "nativesdk"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RELWITHDEBINFO -DWERROR=OFF"

CXXFLAGS += "-DNCURSES_WIDECHAR"

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
