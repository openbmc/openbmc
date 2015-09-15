SUMMARY = "utility functions for the Xsettings protocol"
DESCRIPTION = "Libraries used for applications making use of the Xsettings configuration \
setting propagation protocol. Controls setting of double click timeout, drag-and-drop \
threshold, and default foreground and background colors for all applications running within a \
desktop."
HOMEPAGE = "http://matchbox-project.org/sources/optional-dependencies/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"
SECTION = "x/libs"
LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=7cfac9d2d4dc3694cc7eb605cf32a69b \
                    file://xsettings-client.h;endline=22;md5=7cfac9d2d4dc3694cc7eb605cf32a69b \
                    file://xsettings-common.h;endline=22;md5=7cfac9d2d4dc3694cc7eb605cf32a69b"
DEPENDS = "virtual/libx11"

PR = "r5"

headers = "xsettings-common.h xsettings-client.h"

SRC_URI = "http://downloads.yoctoproject.org/releases/matchbox/optional-dependencies/Xsettings-client-0.10.tar.gz \
        file://MIT-style-license \
        file://link-x11.patch;apply=yes \
        file://disable_Os_option.patch \
        file://obsolete_automake_macros.patch \
"

SRC_URI[md5sum] = "c14aa9db6c097e0306dac97fb7da1add"
SRC_URI[sha256sum] = "f274a4bc969ae192994a856b7f786c6fce96bae77f96c1c2b71dd97599e06e43"

S = "${WORKDIR}/Xsettings-client-0.10"

inherit autotools gettext distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

do_configure_prepend() {
    # This package doesn't ship with its own COPYING file and
    # autotools will install a GPLv2 one instead of the actual MIT-style license here.
    # Add the correct license here to avoid confusion.
    cp -f ${WORKDIR}/MIT-style-license ${S}/COPYING
}

