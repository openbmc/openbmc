require avahi.inc

inherit systemd

SYSTEMD_PACKAGES = "${PN}-daemon ${PN}-dnsconfd"
SYSTEMD_SERVICE_${PN}-daemon = "avahi-daemon.service"
SYSTEMD_SERVICE_${PN}-dnsconfd = "avahi-dnsconfd.service"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://avahi-common/address.h;endline=25;md5=b1d1d2cda1c07eb848ea7d6215712d9d \
                    file://avahi-core/dns.h;endline=23;md5=6fe82590b81aa0ddea5095b548e2fdcb \
                    file://avahi-daemon/main.c;endline=21;md5=9ee77368c5407af77caaef1b07285969 \
                    file://avahi-client/client.h;endline=23;md5=f4ac741a25c4f434039ba3e18c8674cf"

SRC_URI[md5sum] = "d76c59d0882ac6c256d70a2a585362a6"
SRC_URI[sha256sum] = "57a99b5dfe7fdae794e3d1ee7a62973a368e91e414bd0dfa5d84434de5b14804"

DEPENDS += "intltool-native"

PACKAGES =+ "libavahi-gobject"
