SUMMARY = "A library for complex text layout"
DESCRIPTION = "\
    Raqm is a small library that encapsulates the logic for complex text \
    layout and provides a convenient API. It currently provides bidirectional \
    text support (using FriBiDi or SheenBidi), shaping (using HarfBuzz), and \
    proper script itemization. As a result, Raqm can support most writing \
    systems covered by Unicode. \
"
HOMEPAGE = "https://github.com/HOST-Oman/libraqm"
BUGTRACKER = "https://github.com/HOST-Oman/libraqm/issues"
SECTION = "graphics"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=af4502a870b471d3ac78abacb7abfa37"

SRC_URI = "git://github.com/HOST-Oman/libraqm.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "08ee1537e63f93bf926498e12b12ed15ee383216"

DEPENDS = "freetype fribidi harfbuzz"

inherit meson pkgconfig

PACKAGECONFIG ?= ""

PACKAGECONFIG[sheenbidi] = "-Dsheenbidi=true,-Dsheenbidi=false,sheenbidi"
