SUMMARY = "Character Based User Interface for libyui"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING.lgpl-3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://COPYING.lgpl-2.1;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = "git://github.com/libyui/libyui-ncurses.git \
           file://0003-Simplify-ncurses-finding-module.patch \
          "

SRC_URI_append_class-target = " file://0001-Fix-the-error-of-can-t-find-header-file.patch"

SRCREV = "e89fcc53c27c3a3bf559d43190289c3ce4f335b1"

S = "${WORKDIR}/git"

inherit cmake gettext pkgconfig

DEPENDS += "boost libyui ncurses"

BBCLASSEXTEND = "nativesdk"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RELWITHDEBINFO"
EXTRA_OECMAKE_append_toolchain-clang = " -DENABLE_WERROR=OFF"

do_configure_prepend () {
    cd ${S}
    git checkout bootstrap.sh
    sed -i "s#/usr#${PKG_CONFIG_SYSROOT_DIR}${base_prefix}&#" bootstrap.sh
    ./bootstrap.sh
    if [ -e ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib/libyui.so ]; then
        mkdir -p ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib64/
        cp ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib/libyui.so* ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib64/
    fi
    cd -
    sed -i -e "s#\${YPREFIX}#\${PKG_CONFIG_SYSROOT_DIR}${base_prefix}&#" ${S}/CMakeLists.txt
    sed -i -e "s#/usr#${PKG_CONFIG_SYSROOT_DIR}${base_prefix}&#" ${PKG_CONFIG_SYSROOT_DIR}${libdir}/cmake/libyui/LibyuiLibraryDepends-release.cmake
}

do_install_append () {
    if [ "${libdir}" = "${base_prefix}/usr/lib" ] && [ -d ${D}/usr/lib64 ]; then
        mv ${D}/usr/lib64 ${D}/usr/lib
    fi
}

do_install_append_class-nativesdk () {
    mkdir -p ${D}/${base_prefix}
    mv ${D}/usr ${D}/${base_prefix}
}

FILES_${PN} += "${datadir}/*"

FILES_${PN}-dev += "${libdir}/*"
