require libgpiod.inc

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & CC-BY-SA-4.0"
LIC_FILES_CHKSUM = " \
    file://LICENSES/GPL-2.0-or-later.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://LICENSES/LGPL-2.1-or-later.txt;md5=4b54a1fd55a448865a0b32d41598759d \
    file://LICENSES/CC-BY-SA-4.0.txt;md5=fba3b94d88bfb9b81369b869a1e9a20f \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}-2.x:"

SRC_URI += "file://0001-ptest-modify-delays-in-toggle-tests.patch"

SRC_URI[sha256sum] = "b5367d28d045b36007a4ffd42cceda4c358737ef4f2ce22b0c1d05ec57a38392"

# Enable all project features for ptest
PACKAGECONFIG[tests] = "--enable-tests --enable-tools --enable-bindings-cxx --enable-gpioset-interactive,--disable-tests,kmod util-linux glib-2.0 catch2 libedit"
PACKAGECONFIG[gpioset-interactive] = "--enable-gpioset-interactive,--disable-gpioset-interactive,libedit"

PACKAGES =+ "${PN}-ptest-dev"
FILES:${PN}-tools += "${bindir}/gpionotify"
FILES:${PN}-ptest += "${libdir}/libgpiosim.so.*"
FILES:${PN}-ptest-dev += "${includedir}/gpiosim.h"

RRECOMMENDS:${PN}-ptest += "kernel-module-gpio-sim"

do_install_ptest:append() {
    install -m 0644 ${S}/tests/gpiosim/gpiosim.h ${D}${includedir}/gpiosim.h
}
