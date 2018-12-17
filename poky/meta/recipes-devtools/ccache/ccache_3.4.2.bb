require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.adoc;md5=2722abeaf1750dbf175b9491112374e1"

SRC_URI[md5sum] = "9e048f88f3897125864f9a5e1abfb72d"
SRC_URI[sha256sum] = "18a8b14367d63d3d37fb6c33cba60e1b7fcd7a63d608df97c9771ae0d234fee2"

SRC_URI += " \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
"
