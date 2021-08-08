SUMMARY = "Text file viewer similar to more"
DESCRIPTION = "Less is a program similar to more, i.e. a terminal \
based program for viewing text files and the output from other \
programs. Less offers many features beyond those that more does."
HOMEPAGE = "http://www.greenwoodsoftware.com/"
SECTION = "console/utils"

# (GPLv2+ (<< 418), GPLv3+ (>= 418)) | less
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

LICENSE = "GPLv3+ | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://LICENSE;md5=ba01d0cab7f62f7f2204c7780ff6a87d \
                    "
DEPENDS = "ncurses"

SRC_URI = "http://www.greenwoodsoftware.com/${BPN}/${BPN}-${PV}.tar.gz \
	  "

SRC_URI[sha256sum] = "6aadf54be8bf57d0e2999a3c5d67b1de63808bb90deb8f77b028eafae3a08e10"

UPSTREAM_CHECK_URI = "http://www.greenwoodsoftware.com/less/download.html"

inherit autotools update-alternatives

do_install () {
        oe_runmake 'bindir=${D}${bindir}' 'mandir=${D}${mandir}' install
}

ALTERNATIVE:${PN} = "less"
ALTERNATIVE_PRIORITY = "100"
