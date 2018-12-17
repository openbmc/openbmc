SUMMARY = "Plugin providing hotcorners"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.md;beginline=48;endline=53;md5=2c694b8e0f73274b13465b509e4f5427"

inherit xfce-panel-plugin cmake

DEPENDS += "libwnck3"

SRC_URI = "https://github.com/brianhsu/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-CMakeLists.txt-fix-library-install-path.patch"
SRC_URI[md5sum] = "ac31b45cda1867cb6bd69bb285638263"
SRC_URI[sha256sum] = "942684c92f96a158e1417e597947f822769ac56aa1993a8c9f166ee633ef2b8d"

EXTRA_OECMAKE = "-DBASE_LIB_PATH=${baselib}"
