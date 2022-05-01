DESCRIPTION = "RTIMULib is a C++ and Python library that makes it easy to use 9-dof and \
10-dof IMUs with embedded Linux systems"
HOMEPAGE = "https://github.com/RPi-Distro/RTIMULib/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=96cdecb41125f498958e09b72faf318e"

SRC_URI = "git://github.com/RPi-Distro/RTIMULib.git;protocol=http;branch=master;protocol=https \
           file://0001-include-asm-ioctl.h-for-ioctl-define.patch;patchdir=../.. \
           file://0001-setup.py-Port-to-use-setuptools.patch;patchdir=../.. \
          "
SRCREV = "b949681af69b45f0f7f4bb53b6770037b5b02178"

S = "${WORKDIR}/git/Linux/python"
inherit setuptools3
