DESCRIPTION = "Vala DBus Binding Tool"
SECTION = "devel"
DEPENDS = "vala libgee libxml2 intltool-native"
HOMEPAGE = "http://wiki.freesmartphone.org/index.php/Implementations/vala-dbus-binding-tool"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
PE = "1"
INC_PR = "r3"

export XDG_DATA_DIRS = "${STAGING_DATADIR}"

SRC_URI = "${SOURCEFORGE_MIRROR}/freesmartphone/sources/${BP}.tar.bz2"

inherit autotools perlnative pkgconfig

BBCLASSEXTEND = "native"
