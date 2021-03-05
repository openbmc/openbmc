SUMMARY = "Character Based User Interface for libyui"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING.lgpl-3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://COPYING.lgpl-2.1;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = "git://github.com/libyui/libyui-ncurses.git \
          "

SRC_URI_append_class-target = " file://0001-Fix-the-error-of-can-t-find-header-file.patch"

SRCREV = "37d3a1e815a47f536b4f694f139f279cc93a3854"

S = "${WORKDIR}/git"

inherit cmake gettext pkgconfig

DEPENDS += "boost libyui ncurses"

BBCLASSEXTEND = "nativesdk"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RELWITHDEBINFO -DWERROR=OFF"

CXXFLAGS += "-DNCURSES_WIDECHAR"

do_configure_prepend () {
    cd ${S}
    if [ -e ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib/libyui.so ]; then
        mkdir -p ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib64/
        cp ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib/libyui.so* ${PKG_CONFIG_SYSROOT_DIR}${base_prefix}/usr/lib64/
    fi
    cd -
    sed -i -e "s#\${YPREFIX}#\${PKG_CONFIG_SYSROOT_DIR}${base_prefix}&#" ${S}/CMakeLists.txt
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
