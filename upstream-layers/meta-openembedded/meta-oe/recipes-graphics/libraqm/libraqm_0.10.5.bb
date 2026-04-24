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
LIC_FILES_CHKSUM = "file://COPYING;md5=7dc444a99e2824eac906383266fe4fa6"

SRC_URI = "git://github.com/HOST-Oman/libraqm.git;protocol=https;branch=main;tag=v${PV}"
SRCREV = "3a6b891a3db0e0db1364aa38088422f68d8d81e6"

DEPENDS = "freetype fribidi harfbuzz"

inherit meson pkgconfig

PACKAGECONFIG ?= ""

PACKAGECONFIG[sheenbidi] = "-Dsheenbidi=true,-Dsheenbidi=false,sheenbidi"
