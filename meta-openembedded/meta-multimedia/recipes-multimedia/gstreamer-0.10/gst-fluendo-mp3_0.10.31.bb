require gst-fluendo.inc

SUMMARY = "Fluendo closed-format mp3 GStreamer plug-in"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=259a43dd1c9854b71fc396f74699f4d2"
LICENSE_FLAGS = "commercial"

GSTREAMER_DEBUG ?= "--disable-debug"
EXTRA_OECONF += "${GSTREAMER_DEBUG} --with-gstreamer-api=0.10"

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

SRC_URI[md5sum] = "adf0390f3416bb72f91c358528be0c38"
SRC_URI[sha256sum] = "dae0d0559a4e159c0dd92b7e18de059a5783f8d038904c7de4ca6393f7d55c7d"
