require contacts.inc

PR = "r8"

SRC_URI =+ "http://pimlico-project.org/sources/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "aab5affbf93d6fa7b978b323a8d44de0"
SRC_URI[sha256sum] = "9cacec98f8123993033aaa255f3f4c04c86a1be65e487dd21f0aaa54384a6f6d"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/contacts-main.h;endline=18;md5=0371af46fbc72e33575e0072dca5fb19 \
                    file://src/contacts-dbus.c;endline=18;md5=e4da9ac1a0539fafc7df431010904fd5 \
                    file://src/contacts-gtk.c;endline=21;md5=1c2e3f55b215635eff4ba76f7696f8ee"

do_configure_prepend () {
    # It used 8 spaces to instead of a tab, but it doesn't work for us
    sed -i 's/^        $(MAKE) dist distdir=/\t$(MAKE) dist distdir/' Makefile.am
}
