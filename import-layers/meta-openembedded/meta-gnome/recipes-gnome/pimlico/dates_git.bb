require dates.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/dates_view.h;endline=22;md5=29f934f356eb970309cdeb6693b3123a \
                    file://src/dates_hildon.c;endline=19;md5=63938904198b25de429abb65fbdbdb8a \
                    file://src/gconf-bridge.c;endline=22;md5=ad7626c6daf4aec590474a243f4912fa"

SRCREV = "c8e30fa3211dcdfcda48184f4c57746a552b9c7a"
PV = "0.4.11+git${SRCPV}"
PR = "r9"

S = "${WORKDIR}/git"

SRC_URI = "git://git.gnome.org/${BPN} \
           file://dso_linking_change_build_fix.patch \
           file://uclibc-NL_TIME_FIRST_WEEKDAY-absent.patch \
           file://0001-Fix-format-security-warnings.patch \
           "
