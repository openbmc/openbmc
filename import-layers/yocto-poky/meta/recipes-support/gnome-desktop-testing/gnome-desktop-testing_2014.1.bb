SUMMARY = "Test runner for GNOME-style installed tests"
HOMEPAGE = "https://wiki.gnome.org/GnomeGoals/InstalledTests"
LICENSE = "LGPLv2+"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${PV}/${BPN}-${PV}.tar.xz \
           file://0001-gsystem-subprocess.c-Enable-GNU-extensions-in-system.patch \
           file://update-output-syntax.patch \
          "
SRC_URI[md5sum] = "a608ad72a77e23a1aecdfd8d07a94baf"
SRC_URI[sha256sum] = "1a3eed73678dd22d09d6a7ec4f899557df3e8b4a802affa76d0f163b31286539"

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://src/gnome-desktop-testing-runner.c;endline=19;md5=67311a600b83fd0068dfc7e5b84ffb3f"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

PR = "r1"

PACKAGECONFIG ??= ""
PACKAGECONFIG[journald] = "--with-systemd-journal,--without-systemd-journal,systemd,systemd"
