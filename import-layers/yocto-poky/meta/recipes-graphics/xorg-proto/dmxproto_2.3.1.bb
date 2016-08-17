require xorg-proto-common.inc

SUMMARY = "DMX: Distributed Multihead extension headers"

DESCRIPTION = "This package provides the wire protocol for the DMX \
extension.  The DMX extension provides support for communication with \
and control of Xdmx server.  Attributes of the Xdmx server and of the \
back-end screens attached to the server can be queried and modified via \
this protocol."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a3c3499231a8035efd0e004cfbd3b72a \
                    file://dmxproto.h;endline=32;md5=ab8509955c3dd4c65fac728e1b367bc4"

PE = "1"

SRC_URI[md5sum] = "4ee175bbd44d05c34d43bb129be5098a"
SRC_URI[sha256sum] = "e72051e6a3e06b236d19eed56368117b745ca1e1a27bdc50fd51aa375bea6509"
