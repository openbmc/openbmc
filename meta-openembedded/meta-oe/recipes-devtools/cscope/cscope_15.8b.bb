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

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4667b67b483823043fcffa489ea343b"

inherit autotools

DEPENDS += "ncurses"

SRC_URI = "http://downloads.sourceforge.net/project/cscope/cscope/${PV}/${BP}.tar.gz"

SRC_URI[md5sum] = "8f9409a238ee313a96f9f87fe0f3b176"
SRC_URI[sha256sum] = "4889d091f05aa0845384b1e4965aa31d2b20911fb2c001b2cdcffbcb7212d3af"
