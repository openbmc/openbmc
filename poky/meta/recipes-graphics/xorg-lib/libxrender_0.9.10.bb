SUMMARY = "XRender: X Rendering Extension library"

DESCRIPTION = "The X Rendering Extension (Render) introduces digital \
image composition as the foundation of a new rendering model within the \
X Window System. Rendering geometric figures is accomplished by \
client-side tessellation into either triangles or trapezoids.  Text is \
drawn by loading glyphs into the server and rendering sets of them."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8bc71986d3b9b3639f6dfd6fac8f196"

DEPENDS += "virtual/libx11 xorgproto"

PE = "1"

XORG_PN = "libXrender"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "802179a76bded0b658f4e9ec5e1830a4"
SRC_URI[sha256sum] = "c06d5979f86e64cabbde57c223938db0b939dff49fdb5a793a1d3d0396650949"

