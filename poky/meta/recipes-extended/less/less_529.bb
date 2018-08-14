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
                    file://LICENSE;md5=2ef3e4b8dafc85612bc5254b8081e234"
DEPENDS = "ncurses"

SRC_URI = "http://www.greenwoodsoftware.com/${BPN}/${BPN}-${PV}.tar.gz \
	  "

SRC_URI[md5sum] = "32018b53a1f7319ab54349baf80e6aa8"
SRC_URI[sha256sum] = "dba42cd4c38174b7bd0d426d8a39df2be6bcc1ec18946a4642713290f6bf9a0b"

UPSTREAM_CHECK_URI = "http://www.greenwoodsoftware.com/less/download.html"

inherit autotools update-alternatives

do_install () {
        oe_runmake 'bindir=${D}${bindir}' 'mandir=${D}${mandir}' install
}

ALTERNATIVE_${PN} = "less"
ALTERNATIVE_PRIORITY = "100"
