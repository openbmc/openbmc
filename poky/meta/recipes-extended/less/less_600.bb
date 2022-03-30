SUMMARY = "Text file viewer similar to more"
DESCRIPTION = "Less is a program similar to more, i.e. a terminal \
based program for viewing text files and the output from other \
programs. Less offers many features beyond those that more does."
HOMEPAGE = "http://www.greenwoodsoftware.com/"
SECTION = "console/utils"

# (GPL-2.0-or-later (<< 418), GPL-3.0-or-later (>= 418)) | less
# Including email author giving permissing to use BSD
#
# From: Mark Nudelman <markn@greenwoodsoftware.com>
# To: Elizabeth Flanagan <elizabeth.flanagan@intel.com
# Date: 12/19/11
#
# Hi Elizabeth,
# Using a generic BSD license for less is fine with me.
# Thanks,
#
# --Mark
#

LICENSE = "GPL-3.0-or-later | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://LICENSE;md5=38fc26d78ca8d284a2a5a4bbc263d29b \
                    "
DEPENDS = "ncurses"

SRC_URI = "http://www.greenwoodsoftware.com/${BPN}/${BPN}-${PV}.tar.gz \
	  "

SRC_URI[sha256sum] = "6633d6aa2b3cc717afb2c205778c7c42c4620f63b1d682f3d12c98af0be74d20"

UPSTREAM_CHECK_URI = "http://www.greenwoodsoftware.com/less/download.html"

inherit autotools update-alternatives

do_install () {
        oe_runmake 'bindir=${D}${bindir}' 'mandir=${D}${mandir}' install
}

ALTERNATIVE:${PN} = "less"
ALTERNATIVE_PRIORITY = "100"
