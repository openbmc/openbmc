SUMMARY = "XRender: X Rendering Extension library"

DESCRIPTION = "The X Rendering Extension (Render) introduces digital \
image composition as the foundation of a new rendering model within the \
X Window System. Rendering geometric figures is accomplished by \
client-side tessellation into either triangles or trapezoids.  Text is \
drawn by loading glyphs into the server and rendering sets of them."

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8bc71986d3b9b3639f6dfd6fac8f196"

DEPENDS += "virtual/libx11 renderproto xproto xdmcp"

PE = "1"

XORG_PN = "libXrender"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "5db92962b124ca3a8147daae4adbd622"
SRC_URI[sha256sum] = "fc2fe57980a14092426dffcd1f2d9de0987b9d40adea663bd70d6342c0e9be1a"
