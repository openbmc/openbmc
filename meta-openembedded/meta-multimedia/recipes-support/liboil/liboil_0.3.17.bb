SUMMARY = "Library of simple functions optimized for various CPUs"
HOMEPAGE = "http://liboil.freedesktop.org/"
BUGTRACKER = "https://bugs.freedesktop.org/"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=ad80780d9c5205d63481a0184e199a15 \
                    file://liboil/liboil.h;endline=28;md5=95c794a66b88800d949fed17e437d9fb \
                    file://liboil/liboilcpu.c;endline=28;md5=89da69a61d88eedcba066f42353fb75a \
                    file://examples/example1.c;endline=29;md5=9d4dad9fcbbdf0441ee063f8af5170c9 \
                    file://testsuite/trans.c;endline=29;md5=380ecd43121fe3dcc0d8d7e5984f283d"

DEPENDS = "glib-2.0"
PR = "r5"

SRC_URI = "http://liboil.freedesktop.org/download/${BPN}-${PV}.tar.gz \
           file://no-tests.patch \
           file://fix-unaligned-whitelist.patch \
           file://0001-Fix-enable-vfp-flag.patch \
           file://liboil_fix_for_x32.patch \
          "

SRC_URI[md5sum] = "47dc734f82faeb2964d97771cfd2e701"
SRC_URI[sha256sum] = "105f02079b0b50034c759db34b473ecb5704ffa20a5486b60a8b7698128bfc69"

inherit autotools pkgconfig

ARM_INSTRUCTION_SET = "arm"
