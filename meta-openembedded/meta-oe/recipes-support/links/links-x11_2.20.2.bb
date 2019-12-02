# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- COPYING
# +++ COPYING
# @@ -293,7 +293,7 @@
#      <one line to give the program's name and a brief idea of what it does.>
#      Copyright (C) <year>  <name of author>
#  
# -    on, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USAThis program is free software; you can redistribute it and/or modify
# +    This program is free software; you can redistribute it and/or modify
#      it under the terms of the GNU General Public License as published by
#      the Free Software Foundation; either version 2 of the License, or
#      (at your option) any later version.
# 
#

require links.inc

DEPENDS += "virtual/libx11"
RCONFLICTS_${PN} = "links"

inherit features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://links2.desktop \
             http://www.xora.org.uk/oe/links2.png;name=icon"

S = "${WORKDIR}/links-${PV}"

EXTRA_OECONF = "--enable-graphics \
                --with-ssl=${STAGING_LIBDIR}/.. --with-libjpeg \
                --without-libtiff --without-svgalib --without-fb \
                --without-directfb --without-pmshell --without-atheos \
                --with-x --without-gpm"

do_install_append() {
    install -d ${D}/${datadir}/applications
    install -m 0644 ${WORKDIR}/links2.desktop ${D}/${datadir}/applications
    install -d ${D}/${datadir}/pixmaps
    install -m 0644 ${WORKDIR}/links2.png ${D}/${datadir}/pixmaps
}

SRC_URI[md5sum] = "ee39e612249440d0497535d0dafc3c0e"
SRC_URI[sha256sum] = "4b4f07d0e6261118d1365a5a5bfa31e1eafdbd280cfae6f0e9eedfea51a2f424"
SRC_URI[icon.md5sum] = "477e8787927c634614bac01b44355a33"
SRC_URI[icon.sha256sum] = "eddcd8b8c8698aa621d1a453943892d77b72ed492e0d14e0dbac5c6a57e52f47"
