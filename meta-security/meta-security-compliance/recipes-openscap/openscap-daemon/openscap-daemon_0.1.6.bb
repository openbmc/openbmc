# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "The OpenSCAP Daemon is a service that runs in the background."
HOME_URL = "https://www.open-scap.org/tools/openscap-daemon/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40d2542b8c43a3ec2b7f5da31a697b88"
LICENSE = "LGPL-2.1"

DEPENDS = "python3-dbus"

SRCREV = "3fd5c75a08223de35a865d026d2a6980ec9c1d74"
SRC_URI = "git://github.com/OpenSCAP/openscap-daemon.git"

PV = "0.1.6+git${SRCPV}"

inherit setuptools3

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "python"
