require contacts.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/contacts-main.h;endline=20;md5=9dc3531c914fb6d6d4a8d1aee4519fef \
                    file://src/contacts-dbus.c;endline=20;md5=95e02d77f155fbd07a14dba3348b9b03 \
                    file://src/contacts-gtk.c;endline=23;md5=e1ee9b9e72045f2d3aa44cf17313b46e"

SRCREV = "19853893fdb595de6aa59db0d9dc2f9451ed2933"
PV = "0.12+git${SRCPV}"
PR = "r4"

S = "${WORKDIR}/git"

SRC_URI =+ "git://git.gnome.org/${BPN} \
        file://make-382.patch"

S = "${WORKDIR}/git"


