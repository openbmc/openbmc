DESCRIPTION = "LIRC is a package that allows you to decode and send infra-red signals of many commonly used remote controls."
DESCRIPTION_append_lirc = " This package contains the lirc daemon, libraries and tools."
DESCRIPTION_append_lirc-exec = " This package contains a daemon that runs programs on IR signals."
DESCRIPTION_append_lirc-remotes = " This package contains some config files for remotes."
DESCRIPTION_append_lirc-nslu2example = " This package contains a working config for RC5 remotes and a modified NSLU2."
HOMEPAGE = "http://www.lirc.org"
SECTION = "console/network"
LICENSE = "GPLv2"
DEPENDS = "alsa-lib libftdi libusb1 libusb-compat jack portaudio-v19"

SRC_URI = "http://prdownloads.sourceforge.net/lirc/lirc-${PV}.tar.bz2"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
