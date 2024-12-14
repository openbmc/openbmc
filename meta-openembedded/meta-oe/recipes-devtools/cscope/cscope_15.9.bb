# Copyright (C) 2015 Igor Santos <igor.santos@aker.com.br>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Cscope is a text screen based source browser"
DESCRIPTION = "Cscope is a developer's tool for browsing source code. \
               It has an impeccable Unix pedigree, having been originally \
               developed at Bell Labs back in the days of the PDP-11. \
               Cscope was part of the official AT&T Unix distribution for \
               many years, and has been used to manage projects involving 20 \
               million lines of code!"

HOMEPAGE = "http://cscope.sourceforge.net/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4667b67b483823043fcffa489ea343b"

inherit autotools

DEPENDS += "ncurses"

SRC_URI = "http://downloads.sourceforge.net/project/cscope/cscope/v${PV}/${BP}.tar.gz"

SRC_URI[sha256sum] = "c5505ae075a871a9cd8d9801859b0ff1c09782075df281c72c23e72115d9f159"
