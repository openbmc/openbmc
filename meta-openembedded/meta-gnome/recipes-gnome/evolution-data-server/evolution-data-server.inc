SUMMARY = "Evolution database backend server"
HOMEPAGE = "http://www.gnome.org/projects/evolution/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6e689d19255cf0557f3fe7d7068212 \
                    file://src/camel/camel.h;endline=24;md5=342fc5e9357254bc30c24e43ae47d9a1 \
                    file://src/libedataserver/e-data-server-util.h;endline=20;md5=8f21a9c80ea82a4fb80b5f959f672543"

inherit gnomebase upstream-version-is-even

SRC_URI[archive.md5sum] = "23293554e9d92c6c0c78b5568b286efd"
SRC_URI[archive.sha256sum] = "985c4b4b613c50c999a4ca5e9d52478acbea85e1e99bab3a8b6e57ba7eace8f3"
PV = "3.34.4"

