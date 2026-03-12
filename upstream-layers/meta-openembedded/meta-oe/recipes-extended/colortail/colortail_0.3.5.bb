SUMMARY = "Like the tail command line utility but with colors"
DESCRIPTION = "Colortail - like tail but with color highlighting configured via patterns"
HOMEPAGE = "https://github.com/joakim666/colortail"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/joakim666/colortail.git;branch=master;tag=${PV};protocol=https"
SRCREV = "653bbbe3292759f5fb5ae9f769f131c1a320965d"

inherit autotools pkgconfig
