SUMMARY = "Test runner for GNOME-style installed tests"
DESCRIPTION = "Runner provides an execution harness for GNOME installed tests. \
These tests are useful for verifying the functionality of software as \
installed and packaged, and complement rather than replace build-time \
('make check') tests."
HOMEPAGE = "https://wiki.gnome.org/GnomeGoals/InstalledTests"
LICENSE = "LGPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://src/gnome-desktop-testing-runner.c;beginline=1;endline=20;md5=7ef3ad9da2ffcf7707dc11151fe007f4"

SRC_URI = "git://gitlab.gnome.org/GNOME/gnome-desktop-testing.git;protocol=http"
SRCREV = "4decade67b29ad170fcf3de148e41695fc459f48"

DEPENDS = "glib-2.0"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = ",,systemd"
