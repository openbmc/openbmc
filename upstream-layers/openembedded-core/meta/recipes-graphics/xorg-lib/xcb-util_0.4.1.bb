require xcb-util.inc

SRC_URI = "http://xcb.freedesktop.org/dist/${BPN}-${PV}.tar.xz"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/xcb_aux.c;endline=30;md5=ae305b9c2a38f9ba27060191046a6460 \
                    file://src/xcb_event.h;endline=27;md5=627be355aee59e1b8ade80d5bd90fad9"

SRC_URI[sha256sum] = "5abe3bbbd8e54f0fa3ec945291b7e8fa8cfd3cccc43718f8758430f94126e512"
