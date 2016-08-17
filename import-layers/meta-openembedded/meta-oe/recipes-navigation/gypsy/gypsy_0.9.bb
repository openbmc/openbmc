require gypsy.inc

PR = "r2"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.lib;md5=7fbc338309ac38fefcd64b04bb903e34 \
                    file://src/main.c;beginline=1;endline=25;md5=3fe64e27e61b289b77383a54a982cbdd \
                    file://gypsy/gypsy-time.h;beginline=1;endline=24;md5=06432ea19a7b6607428d04d9dadc37fd"

SRC_URI += "http://gypsy.freedesktop.org/releases/gypsy-${PV}.tar.gz \
           file://0001-g_type_init-is-deprecated-for-glib-2.35.0.patch \
          "

SRC_URI[md5sum] = "e2d186df9c2cc3b70a027043e22acf1a"
SRC_URI[sha256sum] = "14e1cbe17351f408538e033ca370b4bf51ccf9c88744e236ddfb271904f154d6"
