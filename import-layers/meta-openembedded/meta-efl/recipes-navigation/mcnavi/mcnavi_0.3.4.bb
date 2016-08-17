SUMMARY = "Free GPS navigation for car and outdoor with OpenStreetMap maps"
HOMEPAGE = "http://www.gps-routes.info/index.php?name=Content&pa=showpage&pid=1"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"
DEPENDS = "ecore evas imlib2 gpsd edje edje-native mysql5 intltool-native"

SRC_URI = "http://www.gps-routes.info/debian/pool/main/m/mcnavi/mcnavi_${PV}.tar.gz"
SRC_URI[md5sum] = "6860cde5c02a9f93c829da4b10e5a226"
SRC_URI[sha256sum] = "ee7f65155e91386175875fc4f72f4bc94e4e9cdb8258112632da94520ef2c349"

S = "${WORKDIR}/${PN}"

inherit autotools

PNBLACKLIST[mcnavi] ?= "BROKEN: latest version 0.3.4 is 3 years old and now broken with more strict ejde_cc from efl-1.13"
# | edje_cc: Error. parse error 0.3.4-r0/mcnavi/data/themes/Default/main.edc:126. invalid state name: 'none'. "default" state must always be first.
# | edje_cc: Error. parse error 0.3.4-r0/mcnavi/data/themes/Default/itinerary.edc:84. invalid state name: 'none'. "default" state must always be first.
# | edje_cc: Error. parse error 0.3.4-r0/mcnavi/data/themes/Default/text.edc:1129. invalid state name: 'none'. "default" state must always be first.
# | edje_cc: Error. parse error 0.3.4-r0/mcnavi/data/themes/Default/fileopen.edc:78. invalid state name: 'off'. "default" state must always be first.
