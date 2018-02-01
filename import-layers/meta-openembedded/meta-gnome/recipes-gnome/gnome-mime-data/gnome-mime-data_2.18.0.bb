SUMMARY = "Base MIME and Application database for GNOME"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://check-mime.pl;endline=26;md5=a95b63c92c33d4ca1af61a315888f450"

inherit gnomebase mime
PR = "r4"

SRC_URI += "file://pkgconfig.patch"

SRC_URI[archive.md5sum] = "541858188f80090d12a33b5a7c34d42c"
SRC_URI[archive.sha256sum] = "37196b5b37085bbcd45c338c36e26898fe35dd5975295f69f48028b1e8436fd7"
GNOME_COMPRESS_TYPE="bz2"

DEPENDS += "shared-mime-info intltool-native glib-2.0-native"
RDEPENDS_${PN} = "shared-mime-info"
