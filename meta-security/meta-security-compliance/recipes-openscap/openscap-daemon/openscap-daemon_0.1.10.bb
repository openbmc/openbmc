# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "The OpenSCAP Daemon is a service that runs in the background."
HOME_URL = "https://www.open-scap.org/tools/openscap-daemon/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40d2542b8c43a3ec2b7f5da31a697b88"
LICENSE = "LGPL-2.1"

DEPENDS = "python3-dbus"

SRCREV = "f25b16afb6ac761fea13132ff406fba4cdfd2b76"
SRC_URI = "git://github.com/OpenSCAP/openscap-daemon.git \
           file://0001-Renamed-module-and-variables-to-get-rid-of-async.patch \
          "

inherit setuptools3

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "openscap scap-security-guide \
                  python3-core python3-dbus \
                  python3-pygobject \
                 "
